
val commonSettings = Seq(
  version := "0.9.0-SNAPSHOT",
  organization := "com.geishatokyo",
  description := "Converter from xls to sql",
  scalaVersion := "2.12.2"
)


lazy val root = (project in file(".")).settings(commonSettings:_*).settings(
  name := "sql-generator",
  libraryDependencies ++= Seq(
    "org.apache.poi" % "poi" % "3.16",
    "org.apache.poi" % "poi-ooxml" % "3.16",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
    "mysql" % "mysql-connector-java" % "5.1.18" % "provided",
    "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided",
    "com.typesafe" % "config" % "1.3.1",
    "com.github.tototoshi" %% "scala-csv" % "1.3.4",
    "org.scalatest" %% "scalatest" % "3.0.2" % "test"
  )
)


lazy val sampleProject = (project in file("sample")).settings(commonSettings:_*).settings(
  name := "sql-generator-sample",
  scalaVersion := "2.12.2"
).dependsOn(root)



