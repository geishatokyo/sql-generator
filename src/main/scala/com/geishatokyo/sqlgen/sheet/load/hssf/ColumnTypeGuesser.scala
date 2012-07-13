package com.geishatokyo.sqlgen.sheet.load.hssf

import com.geishatokyo.sqlgen.sheet.ColumnType

/**
 *
 * User: takeshita
 * Create: 12/07/12 12:07
 */

trait ColumnTypeGuesser {

  def guesserFor(sheetName : String) : String => ColumnType.Value

  def isIgnoreColumn_?(sheetName : String) : String => Boolean

  def isIdColumn_?(sheetName : String) : String => Boolean

}
