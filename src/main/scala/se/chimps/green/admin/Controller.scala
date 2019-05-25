package se.chimps.green.admin

import akka.pattern.ask
import akka.util.Timeout
import se.chimps.bitzness.mini.json.JSON
import se.chimps.cameltow.framework.handlers.Action
import se.chimps.cameltow.framework.responsebuilders.{Error, Ok}
import se.chimps.cameltow.framework.routes.Routes
import se.chimps.green.admin.actors.Apps.{AppsDocument, AppsQuery}
import se.chimps.green.admin.actors.{Apps, Nodes}
import se.chimps.green.admin.actors.Nodes.{DownNodeCommand, NodesDocument, NodesQuery}
import se.chimps.green.api.system.SystemDelegate

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

trait Controller extends JSON {

	implicit def ec:ExecutionContext
	implicit def timeout:Timeout
	implicit def duration:Duration

	def setupRoutes(routes:Routes, system:SystemDelegate):Unit = {
		val nodesActor = system.initWorker(Nodes.worker())
		val appsActor = system.initWorker(Apps.worker())

		routes.GET("/nodes", Action { req =>
			(nodesActor ? NodesQuery).mapTo[NodesDocument]
  			.map(_.nodes.map(n => {
				  SimpleNode(n.uniqueAddress.address.hostPort.split("@")(1), n.roles.toSeq, n.status.toString)
			  }))
  			.map(ns => Ok.json(encode(ns)))
  			.recover {
				  case e:Throwable => Error.text(e.getMessage)
			  }
		})
		routes.GET("/node/down/:address(.*)", Action.sync { req =>
			val address = req.pathParam("address")

			nodesActor ! DownNodeCommand(address)

			Ok.json("")
		})
		routes.GET("/apps", Action { req =>
			(appsActor ? AppsQuery(None)).mapTo[AppsDocument]
  			.map(apps => Ok.json(encode(apps)))
  			.recover {
				  case e:Throwable => Error.text(e.getMessage)
			  }
		})
	}
}

case class SimpleNode(address:String, roles:Seq[String], status:String)