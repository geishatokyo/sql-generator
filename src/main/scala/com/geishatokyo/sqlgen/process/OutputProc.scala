package com.geishatokyo.sqlgen.process

import java.io.File
import java.nio.file.Paths

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait OutputProc[DataType] extends Proc with OutputSupport{

  def dataKey: String

  def output(data: MultiData[DataType], c: Context): Unit


  override def apply(c: Context): Context = {
    val d = c[MultiData[DataType]](dataKey)
    output(d, c)
    c
  }
}

trait OutputSupport {

  def getPath(c: Context, dir: String, name: String): File = {
    if(c.has(Context.ExportDir)) {
      Paths.get(c(Context.ExportDir), dir, name).toFile
    } else if(c.has(Context.WorkingDir)) {
      Paths.get(c.workingDir, dir, name).toFile
    } else {
      Paths.get(dir,name).toFile
    }
  }
}

