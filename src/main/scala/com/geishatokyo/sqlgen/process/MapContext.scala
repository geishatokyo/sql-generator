package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.Context
import collection.mutable

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:56
 */

class MapContext extends Context {

  var innerMap = new mutable.HashMap[String,Any]()

  def +=(kv: (String, Any)) = {
    innerMap.+=( kv._1.toLowerCase -> kv._2)
    this
  }

  def -=(key: String) = {
    innerMap -= key.toLowerCase

    this
  }

  def get(key: String): Option[Any] = {
    innerMap.get(key.toLowerCase)
  }

  def iterator: Iterator[(String, Any)] = {
    innerMap.iterator
  }
}
