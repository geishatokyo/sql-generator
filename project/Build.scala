import sbt._
import Keys._

object XlsConverterBuild extends Build {

  val ScalaVersion = "2.11.8"
  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    version := "0.3.0-SNAPSHOT",
    organization := "com.geishatokyo",
    description := "Converter from xls to sql",
    scalaVersion := ScalaVersion)

  
  lazy val dependencies = Seq(
    "org.apache.poi" % "poi" % "3.14",
    "org.apache.poi" % "poi-ooxml" % "3.14",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.2" % "provided",
    "mysql" % "mysql-connector-java" % "5.1.18" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided",
    "org.scalaz" %% "scalaz-core" % "7.1.2",
    "org.scala-lang" % "scala-reflect" % ScalaVersion,
    "org.scalatest" %% "scalatest" % "2.2.2" % "test"
  )


  lazy val sampleProjet : Project = Project(id = "sample",
    base = file("sample"),
    settings = commonSettings
  ).dependsOn(root)

  lazy val root : Project = Project(id = "sql-generator",
    base =  file("."),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= dependencies
    )
  )



}
