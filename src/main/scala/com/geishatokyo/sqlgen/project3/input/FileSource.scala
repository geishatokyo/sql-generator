package com.geishatokyo.sqlgen.project3.input

import java.io.File

import com.geishatokyo.sqlgen.project3.flow.Input
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

  def flatten(file: File,ext: List[String]) : Array[File] = {
    if(file.isFile) Array(file)
    else{
      file.listFiles().flatMap(f => {
        if(!f.isHidden){
          if(f.isDirectory) flatten(f,ext)
          else if(ext.exists(e => f.getName.endsWith(e))){
            Array(f)
          }else{
            Array[File]()
          }
        }else{
          Array[File]()
        }
      })
    }
  }


}




