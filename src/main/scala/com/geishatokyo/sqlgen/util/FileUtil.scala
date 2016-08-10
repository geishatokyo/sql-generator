package com.geishatokyo.sqlgen.util

import com.geishatokyo.sqlgen.logger.Logger
import java.io._
import org.apache.poi.ss.usermodel
import java.security.MessageDigest
import io.Source
import util.matching.Regex


/**
 *
 * User: takeshita
 * Create: 12/01/30 16:13
 */

object FileUtil extends FileFinder {
  var workingDir = "./"

  var subDirectories = List("swf","png")


  def findFilesWithRegex(dir : File,regex : Regex) : List[File] = {

    val fileOrDirs = dir.listFiles()
    if (fileOrDirs == null) return Nil
    val (files,dirs) = fileOrDirs.partition(_.isFile)
    files.filter( f => regex.findFirstIn(f.getAbsolutePath).isDefined).toList :::
      dirs.flatMap(d => findFilesWithRegex(d,regex)).toList
  }


  def findFile(filename : String) : String = {
    findFileOp(filename).get
  }
  def findFileOp(filename : String) : FileOption = {

    def search( filename : String) : Option[String] = {
      val f = new File(filename)
      if(f.exists()){
        Some(f.getAbsolutePath)
      }else{
        val f2 = new File(workingDir,filename)
        if(f2.exists()){
          Some(f2.getAbsolutePath)
        }else{
          val fromClass = getClass.getClassLoader.getResource(filename)
          if(fromClass != null){
            Some(new File(fromClass.getFile).getAbsolutePath)
          }else{
            None
          }
        }
      }
    }
    search(filename) orElse subDirectories.map( dir => new File(workingDir,joinPath(dir,filename))).find(
      _.exists()
    ).map(_.getAbsolutePath) map(SomeFile(_)) getOrElse {
      NoneFile(List(filename))
    }
  }
  
  def joinPath( dir : String, filename : String) : String = {
    if(dir == null || dir.length == 0) filename
    else new File(dir,filename).getPath
  }

  def toAbsolutePathFromWorkingDir(relativePath : String) = {
    new File(workingDir,relativePath)
  }

  def makeDirs( path : String) : Unit= {
    val f = new File(path)
    if(f.isFile || f.getName.substring(1).contains(".")){
      makeDirs(f.getParent)
    }else{
      if(!f.exists()){
        Logger.log("Create dirs for " + path)
        f.mkdirs()
      }
    }
  }

  def loadFile(filePath : String) : Array[Byte] = {
    val f = new File(findFile(filePath))
    loadFile(f)
  }
  def loadFile(file: File) : Array[Byte] = {
    val input = new FileInputStream(file)
    val fileData = new Array[Byte](input.available())
    input.read(fileData,0,fileData.length)
    input.close()
    fileData

  }
  
  def loadFileAsString(filePath : String, encoding : String) : String = {
    val f = new File(findFile(filePath))
    loadFileAsString(f, encoding)
  }


  def loadFileAsString(f : File) : String = {
    loadFileAsString(f,"utf-8")
  }
  def loadFileAsString(f : File, encoding : String) : String = {
    val input = new FileInputStream(f)
    val fileData = new Array[Byte](input.available())
    input.read(fileData,0,fileData.length)
    input.close
    new String(fileData,encoding)
  }

  def loadFileAsStringFromResource(path: String, encoding: String = "utf8") = {
    val input = getClass.getClassLoader.getResourceAsStream(path)
    Source.fromInputStream(input, encoding).getLines().mkString
  }

  def getFilename( path : String) = {
    val f = new File(path)
    val n = f.getName
    val i = n.lastIndexOf(".")
    if(i > 0){
      n.substring(0,i)
    }else{
      n
    }
  }

  /**
   *
   * @param path
   * @return such ("c:\/program\" , "image" , ".jpg" )
   */
  def splitPathAndNameAndExt( path : String) : (String,String,String) = {
    val f = new File(path)
    val n = f.getName
    val i = n.lastIndexOf(".")
    if(i > 0){
      (f.getParent + File.separator, n.substring(0,i) , n.substring(i))
    }else{
      (f.getParent + File.separator, n , "")
    }
  }
  
  private def _saveTo(filename : String , func : OutputStream => Any) = {
    val dir = new File(filename).getParentFile
    if(!dir.exists()){
      dir.mkdirs()
    }
    val output = new FileOutputStream(filename)
    func(output)
    output.flush()
    output.close()
  }
  
  def saveTo(filename:  String,  lines : List[String]) = {
    _saveTo(filename,output => {
      lines.foreach(line => {
        output.write( (line + "\n").getBytes("utf-8"))
      })
    })
  }
  
  def saveTo(filename : String,  workbook : usermodel.Workbook) = {
    _saveTo(filename,output => {
      workbook.write(output)
    })
  }
  def saveTo(filename : String, data : Array[Byte]) = {
    _saveTo(filename,output => {
      output.write(data)
    })
  }


  def hashSha1(data : Array[Byte]) = {
    val md = MessageDigest.getInstance("SHA1")
    md.update(data)
    md.digest().map("%02x".format(_)).mkString
  }

  def copyTo(from: File, to: File) = {
    val _from = new FileInputStream(from).getChannel
    val _to = new FileOutputStream(to).getChannel

    try {
      _from.transferTo(0, _from.size(), _to)
    } finally {
      safeClose(_from)
      safeClose(_to)
    }
  }

  def safeClose(c : {def close()}) = {
    if(c != null){
      try {
        c.close()
      } catch {
        case e:IOException =>
      }
    }
  }

}