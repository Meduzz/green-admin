package se.chimps.green.admin.actors

import akka.actor.{Actor, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, SubscribeAck, Unsubscribe}
import se.chimps.green.api.system.{AppAlive, AppShutdown}
import se.chimps.green.api.workers.Worker

object Apps {
	class AppsActor extends Actor {
		val mediator = DistributedPubSub(context.system).mediator
		var apps:Seq[AppAlive] = Seq()

		override def receive:Receive = {
			case a:AppAlive => {
				apps = apps.filter(_.id != a.id) ++ Seq(a)
			}
			case a:AppShutdown => apps = apps.filter(_.id != a.id)
			case q:AppsQuery => {
				val caller = sender()
				val result = q.name match {
					case Some(filter) => apps.filter(_.name.contains(filter))
					case None => apps
				}

				caller ! AppsDocument(result)
			}
			case _:SubscribeAck =>
		}

		override def preStart():Unit = {
			mediator ! Subscribe("app.alive", self)
		}

		override def postStop():Unit = {
			mediator ! Unsubscribe("app.alive", self)
		}
	}

	def worker():Worker = new Worker {
		override def props:Props = Props(classOf[AppsActor])
		override def name:String = "AppsActor"
	}

	case class AppsQuery(name:Option[String])
	case class AppsDocument(apps:Seq[AppAlive])
}
