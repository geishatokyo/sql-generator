package com.geishatokyo.sqlgen

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:58
 */

class SQLGenException(m:String,e : Throwable) extends Exception(m,e) {

  def this(m : String) = this(m,null)

}
