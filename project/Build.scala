import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object AvatarXlsConverterBuild extends Build {

  val poi = "org.apache.poi" % "poi" % "3.7"
  val mySqlDriver = "mysql" % "mysql-connector-java" % "5.1.18" % "provided"
  val amazonEC2 = "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided"
  val junit = "junit" % "junit" % "4.8.1" % "test"
  val specs2 = (scalaVersion : String ) => {
    val version = scalaVersion match{
      case "2.9.1" => "1.11"
      case _ => "1.11"
    }
    "org.specs2" %% "specs2" % version % "test"
  }
  lazy val dependencies = Seq(
    poi,mySqlDriver,amazonEC2,
    junit
  )


  val updatePom = TaskKey[Unit]("update-pom")

  val updatePomTask = updatePom <<= makePom map{ file => {
    val moveTo = new java.io.File("pom.xml")
    if(moveTo.exists()){
      println("Delete " + moveTo.getAbsolutePath())
      moveTo.delete()
    }
    println("Move from %s to %s".format(file,moveTo.getAbsolutePath))
    file.renameTo(moveTo)
  }}

  lazy val tasks = Seq(updatePomTask)

  import scala.xml._

  def additionalPom : NodeSeq = {
    XML.loadFile(file("project/pomExtra.xml")).child
  }

  lazy val root = Project(id = "sql-generator",
    base =  file("."),
    settings = Project.defaultSettings ++ assemblySettings ++ Seq(
      version := "0.0.1-SNAPSHOT",
      organization := "com.geishatokyo",
      description := "Converter from xls to sql",
      scalaVersion := "2.9.1",
      crossScalaVersions := Seq("2.9.0","2.9.0-1","2.9.1","2.9.1-1"),
      libraryDependencies ++= dependencies,
      libraryDependencies <<= (scalaVersion,libraryDependencies) { (sv,deps) => { deps :+ specs2(sv)}},
      resolvers ++= Seq(Resolver.mavenLocal),
      pomExtra := additionalPom
    ) ++ tasks
  )
  
}