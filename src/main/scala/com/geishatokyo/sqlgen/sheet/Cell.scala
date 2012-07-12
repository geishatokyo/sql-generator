package com.geishatokyo.sqlgen.sheet

import java.util.Date
import java.text.SimpleDateFormat

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

case class Cell(parent : Sheet,override val initialValue : String) extends VersionedValue(initialValue) {



  def copy(newParent : Sheet) = {
    val c = new Cell(newParent,values.last)
    c.values = this.values
    c
  }

  def :=(d : Date) = {
    this.value_=(d.getTime.toString)
  }


  def asString = value
  def asBool = value match{
    case "true" | "t" | "yes" | "1" => true
    case _ => false
  }
  def asInt = try{
    value.toInt
  }catch{
    case e => 0
  }
  def asLong = try{
    value.toLong
  }catch{
    case e => 0
  }

  def asDouble = try{
    value.toDouble
  }catch{
    case e => 0
  }

  def asDate = try{
    new Date(value.toLong)
  }catch{
    case e => try{
      new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value)
    }catch{
      case e => null
    }
  }

}
