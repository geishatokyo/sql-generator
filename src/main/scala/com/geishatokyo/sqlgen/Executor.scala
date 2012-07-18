package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.process.{Proc}
import process.ProcessProvider
import sheet.Workbook
import java.io.InputStream

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:28
 */

trait Executor[ProjectType <: Project] extends ProcessProvider {


  def preModifyContext(context : Context) {

  }

  def execute(dataLoader : DataLoader[ProjectType]) : Workbook = {
    val workbook = dataLoader.load(project,context)
    execute(workbook)
  }

  private def execute( wb : Workbook) : Workbook = {
    preModifyContext(context)
    val exe = selectExecutor(wb)
    exe(wb)
  }

  def skipOnError(proc : Proc) = proc.skipOnError

  protected def selectExecutor(workbook : Workbook) = executor

  protected def executor : Proc



}
