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
  val datadog = "1.1.13"
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

assemblyJarName in assembly := s"orgs.jar"

libraryDependencies ++= Seq(

  "com.twitter" %% "finatra-http" % versions.finatra,
  "com.twitter" %% "finatra-httpclient" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "io.github.finagle" %% "finagle-postgres" % "0.7.0",

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
  "org.testcontainers" % "postgresql" % "1.6.0" % "test"
)
