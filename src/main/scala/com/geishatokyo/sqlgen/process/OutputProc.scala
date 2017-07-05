package com.geishatokyo.sqlgen.process

import java.io.File

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait OutputProc[DataType] extends Proc{

  def dataKey: String
  def filename: String

  def save(path: String, d: DataType): Unit


  def getExportPath(c: Context): String = {
    if(c.has(OutputProc.ExportDir)) {
      new File(c[String](OutputProc.ExportDir), filename).getAbsolutePath
    } else {
      new File(c.workingDir, filename).getAbsolutePath
    }
  }

  override def apply(c: Context): Context = {
    val d = c[DataType](dataKey)
    val path = getExportPath(c)
    save(path, d)
    c
  }
}

trait WorkbookOutputProc extends OutputProc[Workbook] {
  override def dataKey = Context.Workbook
}

object OutputProc {
  val ExportDir = "exportDir"
}
