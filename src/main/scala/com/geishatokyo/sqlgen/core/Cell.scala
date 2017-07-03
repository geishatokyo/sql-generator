package com.geishatokyo.sqlgen.core
import java.time.ZonedDateTime
import java.time.temporal.{ChronoUnit, TemporalUnit}
import java.util.Date

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.operation._

import scala.collection.mutable
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
        throw new SQLGenException(s"Error on cell at col:${header.name} row:${rowIndex}")
      }
    }
  }

  def +(v: Any) = tryIt{
    this.variable.dataType match{
      case DataType.String => {
        variable.asString + Variable(v).asString
      }
      case DataType.Integer => {
        variable.asLong + Variable(v).asLong
      }
      case DataType.Number => {
        variable.asDouble + Variable(v).asDouble
      }
      case DataType.Date => {
        val dur = Variable(v).asDuration
        variable.asDate.plus(dur.toMicros, ChronoUnit.MILLIS)
      }
      case DataType.Duration => {
        variable.asDuration + Variable(v).asDuration
      }
    }
  }
  def -(v: Any) = tryIt {
    this.variable.dataType match{
      case DataType.Integer => {
        variable.asLong - Variable(v).asLong
      }
      case DataType.Number => {
        variable.asDouble - Variable(v).asDouble
      }
      case DataType.Date => {
        val dur = Variable(v).asDuration
        variable.asDate.minus(dur.toMicros, ChronoUnit.MILLIS)
      }
      case DataType.Duration => {
        variable.asDuration - Variable(v).asDuration
      }
    }
  }
  def *(v: Any) = tryIt(
    this.variable.dataType match{
      case DataType.String => {
        variable.asString * Variable(v).asLong.toInt
      }
      case DataType.Integer => {
        variable.asLong * Variable(v).asLong
      }
      case DataType.Number => {
        variable.asDouble * Variable(v).asDouble
      }
    }
  )
  def /(v: Any) = tryIt{
    this.variable.dataType match{
      case DataType.Integer => {
        variable.asLong / Variable(v).asLong
      }
      case DataType.Number => {
        variable.asDouble / Variable(v).asDouble
      }
    }
  }
  def %(v: Any) = tryIt{
    this.variable.dataType match{
      case DataType.Integer => {
        variable.asLong % Variable(v).asLong
      }
      case DataType.Number => {
        variable.asDouble % Variable(v).asDouble
      }
    }
  }


  def isEmpty = variable == null || variable.isEmpty
  /**
    *
    * @return
    */
  def asLong: Long = variable.asDouble.toLong

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

  /**
    *
    * @return
    */
  def asBool: Boolean = variable.asDouble > 0

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
