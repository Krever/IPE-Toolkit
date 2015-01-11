name := "IPE-Toolkit"

version in ThisBuild       := "0.1.0-SNAPSHOT"

organization in ThisBuild  := "pl.codekratisti"

scalaVersion in ThisBuild  := "2.11.4"

lazy val root =
  project.in(file("."))
    .aggregate(core, javafxBackend)

lazy val core = project.in(file("toolkit-core"))
lazy val javafxBackend = project
  .in(file("toolkit-javafx"))
  .dependsOn(core)


libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
  , "org.mockito" % "mockito-core" % "1.10.19"
  , "com.typesafe.akka" % "akka-actor_2.11" % "2.3.7"
  , "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.7" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)