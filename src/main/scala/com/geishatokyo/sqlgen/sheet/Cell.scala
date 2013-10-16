package com.geishatokyo.sqlgen.sheet

import java.util.Date
import java.text.SimpleDateFormat
import com.geishatokyo.sqlgen.util.TimeUtil
import org.apache.poi.ss.usermodel.DateUtil

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
    this.value_=( TimeUtil.javaDateToExcelTime(d).toString)
  }



  def asString = value
  def asBool = value match{
    case "true" | "t" | "yes" | "1" => true
    case _ => false
  }
  def asInt = try{
    value.toInt
  }catch{
    case e : Throwable => asDouble.toInt
  }
  def asLong = try{
    value.toLong
  }catch{
    case e : Throwable => asDouble.toLong
  }

  def asDouble = try{
    value.toDouble
  }catch{
    case e : Throwable => throw new NumberFormatException("Wrong number format " + value)
  }

  def asDate = try{
    TimeUtil.excelTimeToJavaDate(asDouble)
  }catch{
    case e : Throwable => try{
      new SimpleDateFormat("yyyy/MM/dd HH:mm").parse(value)
    }catch{
      case e : Throwable => null
    }
  }

}
