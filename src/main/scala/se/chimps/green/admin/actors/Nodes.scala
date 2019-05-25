package se.chimps.green.admin.actors

import akka.actor.{Actor, Props}
import akka.cluster.ClusterEvent._
import akka.cluster.{Cluster, Member}
import se.chimps.green.api.workers.Worker

object Nodes {
	class NodesActor extends Actor {
		val cluster = Cluster(context.system)
		var nodes:Seq[Member] = Seq()

		override def receive:Receive = {
			// Member events
			case MemberUp(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case MemberRemoved(member, previousStatus) => nodes = nodes.filter(_.address != member.address)
			case MemberDowned(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case MemberExited(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case MemberJoined(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case MemberLeft(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case UnreachableMember(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)
			case ReachableMember(member) => nodes = nodes.filter(_.address != member.address) ++ Seq(member)

			// Domain events
			case NodesQuery => sender() ! NodesDocument(nodes)
			case DownNodeCommand(address) => {
				nodes.filter(_.address.hostPort.contains(address)).headOption match {
					case Some(member) => cluster.down(member.address)
					case None =>
				}
			}
		}

		override def preStart():Unit = {
			cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
		}

		override def postStop():Unit = cluster.unsubscribe(self)
	}

	def worker():Worker = new Worker {

		override def props:Props = Props(classOf[NodesActor])
		override def name:String = "NodesActor"

	}

	case object NodesQuery
	case class NodesDocument(nodes:Seq[Member])
	case class DownNodeCommand(address:String)
}
