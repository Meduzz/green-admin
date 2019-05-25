name := "green-admin"

version := "201905019"

scalaVersion := "2.12.8"

organization := "se.chimps.green"

resolvers += "se.chimps.cameltow" at "https://yamr.kodiak.se/maven"

resolvers += "se.chimps.bitzness" at "https://yamr.kodiak.se/maven"

resolvers += "se.chimps.green" at "https://yamr.kodiak.se/maven"

libraryDependencies ++= Seq(
	"se.chimps.cameltow" %% "cameltow" % "2.0-beta17",
	"se.chimps.bitzness" %% "bitzness-mini" % "20181222",
	"com.typesafe.akka" %% "akka-cluster-tools" % "2.5.22",
	"se.chimps.green" %% "green-api" % "20190519"
)
