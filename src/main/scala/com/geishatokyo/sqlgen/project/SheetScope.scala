package com.geishatokyo.sqlgen.project

/**
 *
 * User: takeshita
 * Create: 12/07/13 11:36
 */

trait SheetScope {

  var scopedSheet : String
  def inScope_? : Boolean
  protected def beginScope( sheetName : String) : Unit
  protected def endScope( sheetName : String) : Unit

}
