package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.sheet.Workbook

import scala.collection.mutable

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:44
 */

class Context{

  protected var _workingDir : Option[String] = None
  def workingDir : String = _workingDir.getOrElse(".")
  def workingDir_=(v : String) = _workingDir = Some(v)

  def setWorkingDirIfNotSet(v: String) = {
    if(_workingDir.isEmpty) _workingDir = Some(v)
  }


  var references : List[Workbook] = Nil


  def copy() = {
    val c = new Context()
    c._workingDir = _workingDir
    c.references = references
    c
  }

}
