package com.geishatokyo.sqlgen.sheet

import java.util.Date
import java.text.SimpleDateFormat
import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.util.{DateFormat, TimeUtil}
import org.apache.poi.ss.usermodel.DateUtil
import com.geishatokyo.sqlgen.logger.Logger

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

case class Cell(parent : Sheet,var value : Any) {

  var tag : Any = null

  def copy(newParent : Sheet) = {
    new Cell(newParent,value)
  }

  def :=( v : Any) = {
    this.value = v
  }

  def isEmpty = value match {
    case null => true
    case "" => true
    case _ => false
  }


  def asString = if(value != null) {
    value.toString
  }else{
    null
  }
  def asBool = value match{
    case true => true
    case false => false
    case "true" | "t" | "yes" | "1" => true
    case "false" | "f" | "no" | "0" => false
    case _ => false
  }


  def asIntOp = try{
    value match {
      case i : Int => Some(i)
      case l : Long => Some(l.toInt)
      case s : String => Some(s.toInt)
      case d : Double => Some(d.toInt)
      case f : Float => Some(f.toInt)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asInt = {
    niceGet(asIntOp)("Int")
  }


  def asLongOp = try{
    value match {
      case i : Int => Some(i.toLong)
      case l : Long => Some(l)
      case s : String => Some(s.toLong)
      case d : Double => Some(d.toLong)
      case f : Float => Some(f.toLong)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asLong = {
    niceGet(asLongOp)("Long")
  }


  def asDoubleOp: Option[Double] = try{
    value match {
      case i : Int => Some(i.toDouble)
      case l : Long => Some(l.toDouble)
      case s : String => Some(s.toDouble)
      case d : Double => Some(d)
      case f : Float => Some(f.toDouble)
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asDouble = {
    niceGet(asDoubleOp)("Double")
  }

  def asDateOp: Option[Date] = asDateOpOfExcelTime
  def asDate = niceGet(asDateOp)("Date")

  def asDateOpOfUnixTime = try{
    value match {
      case i : Int => Some(new Date(i))
      case l : Long => Some(new Date(l))
      case s : String => DateFormat.parse(s)
      case d : Double => Some(new Date((d * 1000).toLong))
      case f : Float => Some(new Date( (f * 1000).toLong))
      case _ => {
        None
      }
    }
  }catch{
    case e : Throwable => None
  }
  def asDateOpOfExcelTime : Option[Date] = try{
    asDoubleOp.map(d => TimeUtil.excelTimeToJavaDate(d)) orElse{
      DateFormat.parse(value.toString)
    }
  }catch{
    case e : Throwable => {
      Logger.log("Wring date format:" + value)
      None
    }
  }

  def niceGet[T]( op : Option[T])(typeName : String) = {
    op.getOrElse{
      throw new SQLGenException(s"Cell at ${parent.indexOf(this)} in sheet '${parent.name}' is not ${typeName}.")
    }
  }

}
