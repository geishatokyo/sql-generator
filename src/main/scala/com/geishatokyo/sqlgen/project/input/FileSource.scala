package com.geishatokyo.sqlgen.project.input

import java.io.File

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project.flow.{InputData, Input}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.FileUtil

/**
  * Created by takezoux2 on 2016/08/05.
  */
class FileInput(files: File*) extends Input{

  var ext: Option[List[String]] = None

  val extensions = List("csv","xls","xlss")
  val excludeDirs = List("output","target","out",".git",".svn")


  override def read(): List[InputData] = {
    val files = listUpFiles()

    val (csvs,xlss) = files.partition(f => f.getName.endsWith(".csv"))

    val wbs = xlss.map(f => {
      val wb = XLSLoader.load(f)
      val c = context.copy()
      c.setWorkingDirIfNotSet(f.getParent)
      InputData(c,wb)
    }).toList

    val csvWbs = csvs.groupBy(f => {
      f.getParent
    }).map({
      case (dir,files) => {
        val wb = new Workbook()
        wb.name = new File(dir).getName
        files.foreach(f => {
          val sheet = CSVLoader.load(f)
          wb.addSheet(sheet)
        })
        val c = context.copy()
        c.setWorkingDirIfNotSet(dir)
        InputData(c,wb)
      }
    }).toList


    csvWbs ::: wbs

  }

  def listUpFiles() = {
    files.flatMap(f => {
      flatten(f,extensions)
    })
  }

  def flatten(file: File,ext: List[String]) : Array[File] = {
    if(file.isHidden) Array()
    else if(file.isFile && ext.exists(e => file.getName.endsWith(e)))Array(file)
    else if(file.listFiles() == null) Array()
    else{
      if(excludeDirs.contains(file.getName)){
        Array()
      }else {
        file.listFiles().flatMap(f => {
          flatten(f, ext)
        })
      }
    }
  }


}




