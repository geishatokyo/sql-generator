package com.geishatokyo.sqlgen.core
import java.time.ZonedDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.util.Date

import com.geishatokyo.sqlgen.SQLGenException

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.util.Try

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Cell( _parent: Sheet,
            private[core] var _rowIndex: Int,
            private[core] var _columnIndex: Int){

  def parent: Sheet = _parent
  def columnIndex = _columnIndex
  def rowIndex = _rowIndex

  def row = _parent.rows(_rowIndex)
  def column = _parent._columns(_columnIndex)
  def header = _parent.header(columnIndex)

  val note = mutable.Map.empty[String,Any]

  def address = {
    s"${_parent.address}/${header.name}-${rowIndex}"
  }

  private[core] var variable: Variable = NullVar

  def value = variable.raw
  def value_=(v: Any) = {
    variable = Variable(v)
  }

  def :=(v: Any) = this.value = v

  implicit class ZonedDateTimeExt(date: ZonedDateTime) {
    def +(d: Duration) = {
      date.plus(d.toMillis, ChronoUnit.MILLIS)
    }
    def -(d: Duration) = {
      date.minus(d.toMillis, ChronoUnit.MILLIS)
    }
  }

  def throwOpeError(ope: String, passed: Variable) = {
    throw SQLGenException.atCell( this,
      s"Unsupported operation with this:${this.variable.dataType} ${ope} that:${passed.dataType}")

  }


  def +(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asDouble + v.asDouble} orElse
    Try { variable.asDate + v.asDuration} orElse
    Try { variable.asString + v.asString} getOrElse {
      throwOpeError("+",v)
    }
  }
  def -(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asDouble - v.asDouble} orElse
    Try { variable.asDate - v.asDuration} getOrElse {
      throwOpeError("-",v)
    }
  }
  def *(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asLong * v.asLong} orElse
    Try { variable.asDouble * v.asDouble} orElse
    Try { variable.asString * v.asLong.toInt} getOrElse {
      throwOpeError("*",v)
    }
  }
  def /(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asLong / v.asLong} orElse
      Try { variable.asDouble / v.asDouble} getOrElse {
      throwOpeError("/",v)
    }
  }
  def %(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asLong % v.asLong} orElse
      Try { variable.asDouble % v.asDouble} getOrElse {
      throwOpeError("%",v)
    }
  }
  def |(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asBool | v.asBool} orElse
    Try { variable.asLong | v.asLong} getOrElse {
      throwOpeError("|",v)
    }
  }

  def &(_v: Any) = {
    val v = Variable(_v)
    Try { variable.asBool & v.asBool} orElse
      Try { variable.asLong & v.asLong} getOrElse {
      throwOpeError("&",v)
    }
  }


  def tryConversionError[T](toType: String)(func: => T): T = {
    try{
      func
    }catch{
      case t: Throwable => {
        throw SQLGenException.atCell(this,s"Fail to convert to ${toType}",t)
      }
    }
  }

  def isEmpty = variable == null || variable.isEmpty
  /**
    *
    * @return
    */
  def asLong: Long = tryConversionError("Long") { variable.asLong }

  def asInt : Int = tryConversionError("Int") { variable.asLong.toInt }

  /**
    *
    * @return
    */
  def asString: String = tryConversionError("Strihg") { variable.asString}

  /**
    *
    * @return
    */
  def asDouble: Double = tryConversionError("Double") { variable.asDouble}

  /**
    *
    * @return
    */
  def asJavaTime: ZonedDateTime = tryConversionError("Date") { variable.asDate }

  def asDuration = tryConversionError("Duration") { variable.asDuration}

  def asDate = tryConversionError("Date") { variable.asDate }

  def asOldDate: java.util.Date = tryConversionError("java.util.Date") {
    val i = asDate.toInstant
    Date.from(i)
  }

  def dataType = variable.dataType

  /**
    *
    * @return
    */
  def asBool: Boolean = variable.asBool

  def rawValue: Any = variable.raw


  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case c : Cell => this.rawValue == c.rawValue ||
        this.variable.asString == c.variable.asString
      case v: Variable => this.rawValue == v.raw ||
        this.variable.asString == v.asString
      case a: Any => this.rawValue == a || this.asString == a.toString
    }
  }
}

object Cell{

  def apply(_parent: Sheet, row: Int, column: Int, value: Any) : Cell = {
    val cell = new Cell(_parent, row, column)
    cell.value = value
    cell
  }

}
