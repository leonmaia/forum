import sbt.Keys.organization

name := "forum-service"
fork in run := true
version := "0.0.1"
organization := "com.scale"
scalaVersion := "2.12.6"
test in assembly := {}

lazy val versions = new {
  val finatra = "18.8.0"
  val guice = "4.0"
  val logback = "1.1.7"
  val finagleMetrics = "0.0.10"
}

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-Ywarn-unused-import",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

scalacOptions in(Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))

scalacOptions in(Test, console) := (scalacOptions in(Compile, console)).value

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.jcenterRepo
)

coverageMinimum := 80
coverageFailOnMinimum := true
coverageHighlighting := true

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case "META-INF/io.netty.versions.properties" => MergeStrategy.last
  case other => MergeStrategy.defaultMergeStrategy(other)
}

assemblyJarName in assembly := s"forum.jar"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra exclude("commons-logging", "commons-logging"),
  "com.twitter" %% "finatra-httpclient" % versions.finatra,
  "com.twitter" %% "finagle-redis" % versions.finatra,

  "ch.qos.logback" % "logback-classic" % versions.logback,
  "io.github.finagle" %% "finagle-postgres" % "0.7.0",

  "io.github.nafg" %% "slick-migration-api" % "0.4.2",
  "com.1on1development" %% "slick-migration-api-flyway" % "0.4.1",
  "org.postgresql" % "postgresql" % "42.2.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",

  "com.twitter" %% "finatra-http" % versions.finatra % "test",
  "com.twitter" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter" %% "inject-server" % versions.finatra % "test",
  "com.twitter" %% "inject-app" % versions.finatra % "test",
  "com.twitter" %% "inject-core" % versions.finatra % "test",
  "com.twitter" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-server" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter" %% "inject-modules" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.specs2" %% "specs2-mock" % "2.4.17" % "test",
  "org.testcontainers" % "postgresql" % "1.8.3" % "test"
)

val forumDBHost = Option(System.getProperty("forumdb.host")).getOrElse("localhost")
val forumDBPort = Option(System.getProperty("forumdb.port")).getOrElse("5432")
val redisDBPort = Option(System.getProperty("redisdb.port")).getOrElse("6379")

TaskKey[Unit]("start") := (runMain in Compile)
  .toTask(
    s" -DLOG_DIR=. com.scale.forum.server.ServerMain -http.port=:7719 -forumdb.host=$forumDBHost -forumdb.port=$forumDBPort -forumdb.name=forum -forumdb.user=user -forumdb.password=pass -redisdb.port=$redisDBPort")
  .value