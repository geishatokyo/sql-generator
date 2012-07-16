package com.geishatokyo.sqlgen.external

/**
 *
 * User: takeshita
 * Create: 12/03/16 13:16
 */

trait FileUploader {

  def exist_?(filename: String): Boolean

  def upload(key: String, data: Array[Byte]): Boolean

}
