package com.geishatokyo.sqlgen.sheet.load.hssf

/**
 *
 * User: takeshita
 * Create: 12/07/12 12:09
 */

trait NameMapper {

  def isIgnoreSheet_?( sheetName : String) : Boolean

  def mapSheetName(sheetName : String) : String


  def columnNameMapperFor(sheetName : String) : String => String
}
