import sbt._
import Keys._

object XlsConverterBuild extends Build {

  val ScalaVersion = "2.12.2"
  val commonSettings = Defaults.coreDefaultSettings ++ Seq(
    version := "0.9.0-SNAPSHOT",
    organization := "com.geishatokyo",
    description := "Converter from xls to sql",
    name := "sql-generator",
    scalaVersion := ScalaVersion)

  
  lazy val dependencies = Seq(
    "org.apache.poi" % "poi" % "3.14",
    "org.apache.poi" % "poi-ooxml" % "3.14",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
    "mysql" % "mysql-connector-java" % "5.1.18" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided",
    "org.scala-lang" % "scala-reflect" % ScalaVersion,
    "org.scalatest" %% "scalatest" % "3.0.2" % "test"
  )


  lazy val sampleProjet : Project = Project(id = "sample",
    base = file("sample"),
    settings = commonSettings ++ Seq(
      name := "sql-generator-sample"
    )
  ).dependsOn(root)

  lazy val root : Project = Project(id = "sql-generator",
    base =  file("."),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= dependencies
    )
  )



}
