package com.geishatokyo.sqlgen

import project2.input.{WorkbookInput, XLSFileInput, AllXlsFileInDirInput}
import project2.output.{MySQLOutput, XlsOutput, ConsoleOutput}
import sheet.Workbook

/**
 *
 * User: takeshita
 * DateTime: 13/07/12 2:15
 */
package object project2 {

  def withWorkbook(wb : Workbook) = {
    new WorkbookInput(wb)
  }

  def inDir(dir : String) : Input = {
    new AllXlsFileInDirInput(dir)
  }

  def file(file : String) : Input = {
    new XLSFileInput(file)
  }


  def console = {
    new ConsoleOutput()
  }

  def asXls = {
    new XlsOutput()
  }

  def asSql = {
    new MySQLOutput()
  }
}



