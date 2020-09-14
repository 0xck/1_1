import sbt._

object ProjectConfig {

  object versions {
    val akka        = "2.6.8"
    val `akka-http` = "10.2.0"

    val circeVersion = "0.12.3"
    val `akka-http-circe` = "1.34.0"

    val scalactic         = "3.2.0"
    val scalatest         = "3.2.0"
    val scalacheck        = "1.14.3"
    val `scalacheck-1-14` = "3.2.0.0"
    val scalamock         = "5.0.0"

    val `scalacheck-shapeless_1.14` = "1.2.3"
  }

  val testDependencies = Seq(
    "org.scalactic"              %% "scalactic"                 % versions.scalactic                   % Test,
    "org.scalatest"              %% "scalatest"                 % versions.scalatest                   % Test,
    "org.scalacheck"             %% "scalacheck"                % versions.scalacheck                  % Test,
    "org.scalatestplus"          %% "scalacheck-1-14"           % versions.`scalacheck-1-14`           % Test,
    "org.scalamock"              %% "scalamock"                 % versions.scalamock                   % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % versions.`scalacheck-shapeless_1.14` % Test
  )

  val akkaDependencies = Seq(
    "com.typesafe.akka" %% "akka-stream"         % versions.akka,
    "com.typesafe.akka" %% "akka-http"           % versions.`akka-http`,
    "com.typesafe.akka" %% "akka-stream-testkit" % versions.akka,
    "com.typesafe.akka" %% "akka-http-testkit"   % versions.`akka-http`
  )

  val circeDependencies : Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % versions.circeVersion
  ) ++ Seq("de.heikoseeberger" %% "akka-http-circe" % versions.`akka-http-circe`)

  val projectDependencies : Seq[ModuleID] =
    testDependencies ++ akkaDependencies ++ circeDependencies
}