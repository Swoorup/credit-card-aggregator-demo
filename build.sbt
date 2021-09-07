val scala3Version = "3.0.2"

val logBackClassicV = "1.2.5"
val catsV = "2.6.1"
val catsEffectV = "3.2.7"
val sttpV = "3.3.14"
val tapirV = "0.19.0-M8"
val declineV = "2.1.0"
val circeV = "0.14.1"
val munitV = "0.7.29"
val munitCatsEffectV = "1.0.5"
val wiremockV = "2.27.2"

Global / onChangedBuildSource := ReloadOnSourceChanges
enablePlugins(JavaServerAppPackaging)

lazy val root = project
  .in(file("."))
  .settings(
    name := "credit-card-aggregator",
    version := "0.1.0",
    fork := true,
    run / connectInput := true,
    ThisBuild / scalaVersion := scala3Version,
    ThisBuild / githubWorkflowPublishTargetBranches := Seq(),
    libraryDependencies ++=
      Seq(
        "ch.qos.logback" % "logback-classic" % logBackClassicV,

        // functional programming
        "org.typelevel" %% "cats-core" % catsV,
        "org.typelevel" %% "cats-effect" % catsEffectV,

        "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirV,
        "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % tapirV,
        "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % tapirV,

        "com.softwaremill.sttp.client3" %% "core" % sttpV,
        "com.softwaremill.sttp.client3" %% "async-http-client-backend-fs2" % sttpV,
        "com.softwaremill.sttp.client3" %% "circe" % sttpV,

        // command line parser
        "com.monovore" %% "decline" % declineV,
        "com.monovore" %% "decline-effect" % declineV,

        // json utilities
        "io.circe" %% "circe-core" % circeV,
        "io.circe" %% "circe-jawn" % circeV,
        "io.circe" %% "circe-generic" % circeV,

        // testing
        "org.scalameta" %% "munit" % munitV % "test",
        "org.scalameta" %% "munit-scalacheck" % munitV % "test",
        "org.typelevel" %% "munit-cats-effect-3" % munitCatsEffectV % "test",
        "com.github.tomakehurst" % "wiremock" % wiremockV % "test",
      )
  )
