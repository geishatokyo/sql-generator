package com.geishatokyo.sqlgen.util

import java.io.File
import java.util.StringTokenizer

/**
 *
 * User: takeshita
 * Create: 12/07/13 19:47
 */

object I18NUtil {



  def findI18NFiles(dir : String,baseName : String) : List[String] = {
    val currentDir = new File(dir)
    val files = currentDir.listFiles().flatMap( f=> {
      filterI18NFiles(baseName,f)
    })
    files.toList
  }
  def findI18NFiles(baseFilename : String) : List[String] = {
    val (dir,baseName,ext) = FileUtil.splitPathAndNameAndExt(baseFilename)
    findI18NFiles(dir,baseName)
  }

  private def filterI18NFiles(baseName : String,f : File) : Option[String] = {
    val filename = f.getAbsolutePath
    val (dir,fn,ext) = FileUtil.splitPathAndNameAndExt(filename)
    if (fn.startsWith(baseName) && fn != baseName){
      Some(filename)
    }else{
      None
    }

  }

}
