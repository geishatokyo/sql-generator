package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.Context

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:06
 */
trait Output {

  def write(context : Context,w : Workbook) : Unit
}
