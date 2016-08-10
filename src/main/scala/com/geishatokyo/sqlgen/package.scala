package com.geishatokyo

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.geishatokyo.sqlgen.project.flow.Output
import com.geishatokyo.sqlgen.project.input.FileSource
import com.geishatokyo.sqlgen.project.output.{ConsoleOutput, SQLOutput, XlsOutput}

/**
 * Created by takezoux2 on 15/05/05.
 */
package object sqlgen {


  def file(file: String) = {
    new FileSource(new File(file)).asInput
  }
  def files(fs: List[String]) = {
    new FileSource(fs.map(new File(_)):_*).asInput
  }
  def lastDirIn(d: String) = {
    val file = new File(d)
    val dir = file.listFiles().filter(f => !f.isHidden && f.isDirectory).sortBy(_.getName).last
    new FileSource(dir).asInput
  }

  def asMySQL : Output = {
    SQLOutput.mysql()
  }
  def asSqlite : Output = {
    SQLOutput.sqlite()
  }

  def asMySQL(path : String) : Output = {
    val output = SQLOutput.mysql()
    output.path = path
    output
  }
  def asSqlite(path : String) : Output = {
    val output = SQLOutput.sqlite()
    output.path = path
    output
  }

  def asXLS(dirPath : String) : Output = {
    new XlsOutput(dirPath,false)
  }

  def asXLSX(dirPath : String) : Output = {
    new XlsOutput(dirPath,true)
  }
  def console : Output = {
    new ConsoleOutput
  }

  def now = {
    new SimpleDateFormat("YYYY/MM/DD HH:mm:ss").format(new Date)
  }

  def today = {
    new SimpleDateFormat("YYYY/MM/DD").format(new Date)
  }

}
