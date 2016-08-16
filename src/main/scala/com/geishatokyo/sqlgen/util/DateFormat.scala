package com.geishatokyo.sqlgen.util

import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by takezoux2 on 14/12/19.
 */
object DateFormat {

  var formats = List(
    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
    new SimpleDateFormat("yyyy-MM-dd HH:mm"),
    new SimpleDateFormat("yyyy-MM-dd"),
    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"),
    new SimpleDateFormat("yyyy/MM/dd HH:mm"),
    new SimpleDateFormat("yyyy/MM/dd")
  )

  def addFormat(format : String) = {
    formats = new SimpleDateFormat(format) :: formats
  }

  def parse(v : String) : Option[Date] = {
    if(v == null || v.length == 0) return None
    formats.view.map(f => {
      try{
        Some(f.parse(v))
      }catch{
        case e : Throwable => None
      }
    }).find(_.isDefined).flatten
  }
}
