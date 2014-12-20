name := "IPE-Toolkit"

version in ThisBuild       := "0.1.0-SNAPSHOT"

organization in ThisBuild  := "pl.codekratisti"

scalaVersion in ThisBuild  := "2.11.4"

lazy val root =
  project.in( file(".") )
    .aggregate(core, javafxBackend)

lazy val core = project.in(file("toolkit-core"))
lazy val javafxBackend = project
  .in(file("toolkit-javafx"))
  .dependsOn(core)


libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

scalacOptions in Test ++= Seq("-Yrangepos")

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo)