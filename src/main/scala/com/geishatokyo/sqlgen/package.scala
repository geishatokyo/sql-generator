package com.geishatokyo

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process.input.{DirectoryLoaderInput, FileLoaderInput, WorkbookInput}
import com.geishatokyo.sqlgen.process.output.ConsoleOutputProc
import com.geishatokyo.sqlgen.process.{Proc, ProjectProc}

/**
  * usage:
  *
  * file("hoge.xls",
  *
  *
 * Created by takezoux2 on 15/05/05.
 */
package object sqlgen {


  def fromFile(file: String*): Proc = {
    FileLoaderInput.auto(file:_*)
  }

  def inDir(dir: String): Proc = {
    DirectoryLoaderInput.auto(dir)
  }

  def workbook(w: Workbook): Proc = {
    new WorkbookInput(w)
  }

  implicit def projectProc(p: Project): Proc = {
    new ProjectProc(p)
  }


  def showConsole = {
    new ConsoleOutputProc()
  }



}