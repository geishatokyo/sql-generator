package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.process.{Proc, Input}
import process.ProcessProvider
import sheet.Workbook
import java.io.InputStream

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:28
 */

trait Executor[ProjectType <: Project] extends Input with ProcessProvider {


  def preModifyContext(context : Context) {

  }

  def execute(filename : String) : Workbook  = {
    execute(load(filename))
  }

  def execute(inputStream : InputStream) : Workbook = {
    execute(load(inputStream))
  }
  def executeFromBytes(v : Array[Byte]) = {
    execute(loadFromBytes(v))
  }
  def executeFromString(v : String) = {
    execute(loadFromString(v))
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
