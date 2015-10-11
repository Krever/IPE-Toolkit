name := "ipe-toolkit"

version in ThisBuild := "0.1.11-SNAPSHOT"

organization in ThisBuild  := "pl.codekratisti"

scalaVersion in ThisBuild  := "2.11.4"

val akkaVersion = "2.3.11"

libraryDependencies in ThisBuild ++= Seq(
  "com.typesafe.akka"            %% "akka-actor"    % akkaVersion
  , "org.controlsfx"             %  "controlsfx"     % "8.40.9"
  , "org.scala-lang.modules"     %% "scala-xml"     % "1.0.5"
  , "org.scalatest"              %% "scalatest"     % "2.2.1"      % "test"
  , "org.mockito"                %  "mockito-core"   % "2.0.3-beta" % "test"
  , "com.typesafe.akka"          %% "akka-testkit"  % akkaVersion  % "test"
  , "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  , "com.google.guava"           %  "guava"          % "12.0"
)

lazy val root = project.in(file("."))

lazy val sample = project.in(file("sample"))
  .dependsOn(root)
