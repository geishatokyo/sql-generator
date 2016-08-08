package com.geishatokyo.sqlgen

import scala.collection.mutable

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:44
 */

class Context{

  protected var _workingDir : String = ""
  def workingDir : String = _workingDir
  def workingDir_=(v : String) = this._workingDir = v

}
