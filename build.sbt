ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaProjet"
  )

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-ast" % "4.0.7",
  "org.json4s" %% "json4s-native" % "4.0.7"
)