import sbt._
import Keys._

object AvatarXlsConverterBuild extends Build {

  
  
  lazy val dependencies = Seq(
    "org.apache.poi" % "poi" % "3.7",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.2" % "provided",
    "mysql" % "mysql-connector-java" % "5.1.18" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided",
    "org.scalatest" %% "scalatest" % "2.2.2" % "test"
  )


  lazy val root = Project(id = "sql-generator",
    base =  file("."),
    settings = Project.defaultSettings ++ Seq(
      version := "0.2.0-SNAPSHOT",
      organization := "com.geishatokyo",
      description := "Converter from xls to sql",
      scalaVersion := "2.11.6",
      libraryDependencies ++= dependencies
    )
  )
  
}
