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
    s"${parent.parent.name}/${parent.name}/${header.name}-${rowIndex}"
  }

  private[core] var variable: Variable = NullVar

  def value = variable.raw
  def value_=(v: Any) = {
    variable = Variable(v)
  }

  def :=(v: Any) = this.value = v

  def tryIt[T](func: => T): T = {
    try{
      func
    } catch {
      case t: Throwable => {
        throw new SQLGenException(s"Error on cell at col:${header.name} row:${rowIndex}",t, parent.parent)
      }
    }
  }

  implicit class ZonedDateTimeExt(date: ZonedDateTime) {
    def +(d: Duration) = {
      date.plus(d.toMillis, ChronoUnit.MILLIS)
    }
    def -(d: Duration) = {
      date.minus(d.toMillis, ChronoUnit.MILLIS)
    }
  }

  def throwOpeError(ope: String, passed: Variable) = {
    throw new SQLGenException(
      s"${address} - Unsupported operation with this:${this.variable.dataType} ${ope} that:${passed.dataType}",parent.parent)

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



  def isEmpty = variable == null || variable.isEmpty
  /**
    *
    * @return
    */
  def asLong: Long = variable.asLong

  /**
    *
    * @return
    */
  def asString: String = variable.asString

  /**
    *
    * @return
    */
  def asDouble: Double = variable.asDouble

  /**
    *
    * @return
    */
  def asJavaTime: ZonedDateTime = variable.asDate

  def asDuration = variable.asDuration

  /**
    *
    * @return
    */
  def asBool: Boolean = variable.asBool

  def rawValue: Any = variable.raw

  private def throwE(message: String) = {
    val m = s"${this.parent.parent.name}/${this.parent.name} Row:${this.rowIndex} Column:${this.header.name} -- ${message}"
    throw new SQLGenException(m)
  }

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
