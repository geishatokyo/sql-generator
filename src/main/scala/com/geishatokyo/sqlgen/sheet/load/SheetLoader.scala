package com.geishatokyo.sqlgen.sheet.load

import com.geishatokyo.sqlgen.sheet.Workbook
import java.io.InputStream


/**
 *
 * User: takeshita
 * Create: 12/07/12 11:26
 */

trait SheetLoader {

  def load( input : InputStream) : Workbook
}
