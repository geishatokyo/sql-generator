package com.geishatokyo.sqlgen

import sheet.Workbook

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:58
 */

class SQLGenException(m:String,e : Throwable,workbook : Workbook) extends Exception(m,e) {

  def this(m : String) = this(m,null,null)

  def this(m : String , workbook : Workbook) = this(m,null,workbook)

}
