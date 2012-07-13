package com.geishatokyo.sqlgen.sheet

import com.geishatokyo.sqlgen.SQLGenException

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:59
 */

class HeaderNotFoundException(sheetName : String,headerName : String) extends
  SQLGenException("Header:%s is not found in Sheet:%s".format(headerName,sheetName)) {

}
