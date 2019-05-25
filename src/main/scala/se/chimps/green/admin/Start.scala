package se.chimps.green.admin

import se.chimps.green.api.system.GreenSystem

import scala.sys.ShutdownHookThread

object Start {
	def main(args:Array[String]):Unit = {
		val green = new GreenSystem
		green.start(new AdminApp)

		ShutdownHookThread {
			green.stop()
		}
	}
}
