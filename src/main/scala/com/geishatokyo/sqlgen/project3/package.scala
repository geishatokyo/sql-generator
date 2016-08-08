package com.geishatokyo.sqlgen

import java.io.File

import com.geishatokyo.sqlgen.project3.flow.Output
import com.geishatokyo.sqlgen.project3.input.{FileSource}
import com.geishatokyo.sqlgen.project3.output.{XlsOutput, ConsoleOutput, SQLOutput}

/**
 * Created by takezoux2 on 15/05/05.
 */
package object project3 {


  def file(file: String) = {
    new FileSource(new File(file))
  }
  def files(fs: List[String]) = {
    new FileSource(fs.map(new File(_)):_*)
  }
  def lastDirIn(d: String) = {
    val file = new File(d)
    val dir = file.listFiles().filter(f => !f.isHidden && f.isDirectory).sortBy(_.getName).last
    new FileSource(dir)
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

  def asXLS(filename : String) : Output = {
    new XlsOutput(filename)
  }
  def console : Output = {
    new ConsoleOutput
  }

}
