val scala3Version = "3.0.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "credit-card-aggregator",
    version := "0.1.0",
    ThisBuild / scalaVersion := scala3Version,
    ThisBuild / githubWorkflowPublishTargetBranches := Seq(),
    libraryDependencies ++=
      Seq(
        "ch.qos.logback" % "logback-classic" % "1.2.5",

        // functional programming
        "org.typelevel" %% "cats-core" % "2.6.1",
        "org.typelevel" %% "cats-effect" % "3.2.7",

        "com.softwaremill.sttp.tapir" %% "tapir-core" % "0.19.0-M8",
        "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "0.19.0-M8",
        "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "0.19.0-M8",

        "com.softwaremill.sttp.client3" %% "core" % "3.3.14",
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-cats" % "3.3.14",
        "com.softwaremill.sttp.client3" %% "circe" % "3.3.14",

        // command line parser
        "com.monovore" %% "decline" % "2.1.0",
        "com.monovore" %% "decline-effect" % "2.1.0",

        // json utilities
        "io.circe" %% "circe-core" % "0.14.1",
        "io.circe" %% "circe-jawn" % "0.14.1",
        "io.circe" %% "circe-generic" % "0.14.1",

        // testing
        "org.scalameta" %% "munit" % "0.7.29" % "test",
        "org.typelevel" %% "munit-cats-effect-3" % "1.0.5" % "test",
      )
  )
