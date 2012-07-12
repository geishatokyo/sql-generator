package com.geishatokyo.sqlgen.sheet

import com.geishatokyo.sqlgen.SQLGenException

/**
 *
 * User: takeshita
 * Create: 12/07/12 0:20
 */

class SheetNotFoundException(sheetName : String)
  extends SQLGenException("Sheet:%s is not found".format(sheetName),null) {

}
