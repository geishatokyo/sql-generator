package com.geishatokyo.sqlgen.util

import java.io.FileNotFoundException

/**
 *
 * User: takeshita
 * Create: 12/03/16 12:56
 */

trait FileFinder {

  def findFile(filename : String) : String

  def findFileOp(filename : String) : FileOption

}

abstract class FileOption{

  def orElse( fileOption : FileOption) : FileOption

  def get : String

  def getOrElse( v : String) : String
  
  def foreach( f : String => Any) : Unit

  def map[A](f : String => A) : Option[A]
  
}

final case class SomeFile( filePath : String) extends FileOption{
  def orElse(fileOption: FileOption) = {
    this
  }

  def get = filePath

  def getOrElse(v: String) = filePath

  def foreach(f: (String) => Any) {f(filePath)}

  def map[A](f: (String) => A) = Some(f(filePath))
}

final case  class NoneFile( missingPaths : List[String]) extends FileOption{
  def orElse(fileOption: FileOption) = {
    fileOption match{
      case SomeFile(f) => {
        SomeFile(f)
      }
      case NoneFile(paths) => {
        NoneFile(missingPaths ::: paths)
      }
    }
  }

  def get = {
    throw new FileNotFoundException(missingPaths.mkString(",") + " are not found")
  }

  def getOrElse(v: String) = v

  def foreach(f: (String) => Any) {}

  def map[A](f: (String) => A) = None
}