package se.chimps.green.admin

import java.util.concurrent.TimeUnit

import akka.util.Timeout
import io.undertow.Undertow
import se.chimps.cameltow.Cameltow
import se.chimps.green.api.GreenApp
import se.chimps.green.api.system.SystemDelegate

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class AdminApp extends GreenApp with Controller {
	implicit val ec:ExecutionContext = ExecutionContext.global
	implicit val timeout:Timeout = Timeout(1L, TimeUnit.SECONDS)
	implicit val duration:Duration = Duration(1L, TimeUnit.SECONDS)

	val name:String = "AdminApp"
	val version:String = "20190518"

	private var server:Undertow = _

	override def start(system:SystemDelegate):Unit = {
		val routes = Cameltow.routes()

		setupRoutes(routes, system)

		server = Cameltow.defaults("AdminApp")
  			.handler(routes.handler)
  			.listen()

		server.start()
	}

	override def stop():Unit = {
		server.stop()
	}
}
