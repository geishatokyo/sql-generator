import sbt._
import Keys._

object AvatarXlsConverterBuild extends Build {

  
  def ideaPluginSettings = {
    import org.sbtidea.SbtIdeaPlugin
    SbtIdeaPlugin.settings
  }
  
  val specs2 = (scalaVersion : String ) => {
    val version = scalaVersion match{
      case "2.10.0" => "2.0"
      case "2.9.1-1" => "1.11"
      case "2.9.1" => "1.11"
      case "2.9.0-1" => "1.8.2"
      case "2.9.0" => "1.7.1"
      case _ => "1.11"
    }
    "org.specs2" %% "specs2" % version % "test"
  }
  lazy val dependencies = Seq(
    "org.apache.poi" % "poi" % "3.7",
    "mysql" % "mysql-connector-java" % "5.1.18" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided"
  )


  lazy val root = Project(id = "sql-generator",
    base =  file("."),
    settings = Project.defaultSettings ++ ideaPluginSettings ++ Seq(
      version := "0.1.0-SNAPSHOT",
      organization := "com.geishatokyo",
      description := "Converter from xls to sql",
      scalaVersion := "2.10.0",
      crossScalaVersions := Seq("2.9.0","2.9.0-1","2.9.1","2.9.1-1","2.10.0"),
      libraryDependencies ++= dependencies,
      libraryDependencies <<= (scalaVersion,libraryDependencies) { (sv,deps) => { deps :+ specs2(sv)}}
     
    )
  )
  
}