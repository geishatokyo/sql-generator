import sbt._
import Keys._

object AvatarXlsConverterBuild extends Build {

  val poi = "org.apache.poi" % "poi" % "3.7"
  val mySqlDriver = "mysql" % "mysql-connector-java" % "5.1.18" % "provided"
  val amazonEC2 = "com.amazonaws" % "aws-java-sdk" % "1.2.6" % "provided"
  val junit = "junit" % "junit" % "4.8.1" % "test"
  val specs2 = (scalaVersion : String ) => {
    val version = scalaVersion match{
      case "2.9.1-1" => "1.11"
      case "2.9.1" => "1.11"
      case "2.9.0-1" => "1.8.2"
      case "2.9.0" => "1.7.1"
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
  
  //val Maven3Patterns = Patterns(Nil,"[organisation]/[module](_[scalaVersion])(_[sbtVersion])/[revision]/[artifact]-[revision](-[timestamp])(-[classifier]).[ext]" :: Nil,true)
  val Maven3Patterns = Patterns(Nil,"[organisation]/[module](_[scalaVersion])(_[sbtVersion])/[revision]/[filename](-[classifier]).[ext]" :: Nil,true)

  lazy val root = Project(id = "sql-generator",
    base =  file("."),
    settings = Project.defaultSettings ++ Seq(
      version := "0.0.1-SNAPSHOT",
      organization := "com.geishatokyo",
      description := "Converter from xls to sql",
      scalaVersion := "2.9.1",
      crossScalaVersions := Seq("2.9.0","2.9.0-1","2.9.1","2.9.1-1"),
      /*publishTo := Some(Resolver.file("localMaven",Path.userHome / ".m2" / "repository")(
        Maven3Patterns
      )),//*/
      publishTo := Some(Resolver.url("geishatokyo-nexus-snapshot", 
        new URL("http://nexus.geishatokyo.com/nexus/content/repositories/geishatokyo-unstable-snapshots") 
      )(Maven3Patterns)),//*/
      libraryDependencies ++= dependencies,
      libraryDependencies <<= (scalaVersion,libraryDependencies) { (sv,deps) => { deps :+ specs2(sv)}},
      resolvers ++= Seq(Resolver.mavenLocal),
      /*artifactName := { (sv,module,artifact) => {
        import java.util.Date
        import java.text.SimpleDateFormat
        val timestamp = new SimpleDateFormat("yyyyMMDD.HHmmss-1").format(new Date)
        artifact.name + "_" + sv + "-" + module.revision + "-" + timestamp + "." + artifact.extension
      }}*/
      /*publishConfiguration <<= publishConfiguration map { conf => {
        println(conf.artifacts)
        import java.util.Date
        import java.text.SimpleDateFormat
        val timestamp = new SimpleDateFormat("yyyyMMDD.HHmmss-1").format(new Date)
        val artifacts = conf.artifacts.map({
          case (artifact,file) => {
            artifact.copy(extraAttributes = artifact.extraAttributes ++ Map("timestamp" -> timestamp)) -> file
          }
        }).toMap
        
        new PublishConfiguration(conf.ivyFile,conf.resolverName,artifacts,conf.checksums,conf.logging)
      }}*/
      packagedArtifacts <<= (packagedArtifacts,version,scalaVersion) map { (artifacts,version,sv) => {
        
        import java.util.Date
        import java.text.SimpleDateFormat
        val now = new Date
        val timestampForPom = new SimpleDateFormat("yyyyMMddHHmmss").format(now)
        val timestamp = new SimpleDateFormat("yyyyMMdd.HHmmss").format(now)
        scala.collection.immutable.ListMap.empty[Artifact,File] ++ artifacts.map({
          case (artifact,file) => {
            val filename = artifact.name + "_" + sv + "-" + version
            artifact.copy(extraAttributes = artifact.extraAttributes ++ Map("filename" -> filename)) -> file
          }
        
        }) ++ artifacts.collect({
          case (artifact,file) if artifact.`type` == "pom" || artifact.`type` == "jar" => {
            val filename = artifact.name + "_" + sv + "-" + version.stripSuffix("-SNAPSHOT") + "-" + timestamp + "-1"
            artifact.copy(extraAttributes = artifact.extraAttributes ++ Map("filename" -> filename)) -> file
          }
        }) ++ Map(createMetadata(artifacts.head._1, version,timestamp,timestampForPom,sv))
        
         
      }}
    ) ++ tasks
  )
  
  def createMetadata(base : Artifact,version : String,timestampStr : String,timestampForPom : String,sv : String) = {
    val metadata = <metadata modelVersion="1.1.0">
    <groupId>com.geishatokyo.chabi</groupId>
    <artifactId>{base.name}_{sv}</artifactId>
    <version>{version}</version>
    <versioning>
        <snapshot>
            <timestamp>{timestampStr}</timestamp>
            <buildNumber>1</buildNumber>
        </snapshot>
        <lastUpdated>{timestampForPom}</lastUpdated>
        <snapshotVersions>
            <snapshotVersion>
                <extension>jar</extension>
                <value>{version.stripSuffix("-SNAPSHOT")}-{timestampStr}-1</value>
                <updated>{timestampForPom}</updated>
            </snapshotVersion>
        </snapshotVersions>
    </versioning>
</metadata>
    
    val file = new java.io.File("target/scala-%s/maven-metadata.xml".format(sv))
    IO.write(file,metadata.toString)
    base.copy(extension = "xml",`type` = "metadata",configurations = Nil,extraAttributes = Map("filename" -> "maven-metadata")) -> file
  
  }
  
}