package com.geishatokyo.sqlgen.project.input

import java.io.File

import com.geishatokyo.sqlgen.project.flow.Input
import com.geishatokyo.sqlgen.util.FileUtil

/**
  * Created by takezoux2 on 2016/08/05.
  */
class FileSource(files: File*) {

  var ext: Option[List[String]] = None

  def listUpFiles(ext: List[String]) = {
    files.flatMap(f => {
      flatten(f,ext)
    })
  }

  def asCsv() : Input = {
    val csvs = listUpFiles(ext.getOrElse(List("csv"))).map(file => {
      FileUtil.loadFileAsString(file)
    })
    new CSVInput(csvs.toList)
  }
  def asXls() : Input = {
    val files = listUpFiles(ext.getOrElse(List("xls","xlsx")))
    new XLSInput(files.toList)
  }

  def asInput() : Input = {
    val xlss = listUpFiles(ext.getOrElse(List("xls","xlsx")))
    if(xlss.size > 0){
      new XLSInput(xlss.toList)
    }else{
      asCsv()
    }
  }

  def flatten(file: File,ext: List[String]) : Array[File] = {
    if(file.isHidden) Array()
    else if(file.isFile && ext.exists(e => file.getName.endsWith(e)))Array(file)
    else if(file.listFiles() == null) Array()
     else{
      file.listFiles().flatMap(f => {
        flatten(f,ext)
      })
    }
  }


}




