package com.geishatokyo.sqlgen.core

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import scala.concurrent.duration._

/**
  * Created by takezoux2 on 2017/06/08.
  */
trait Variable {



  def dataType: DataType

  def asDouble: Double
  def asLong: Long
  def asString: String
  def asDate: ZonedDateTime
  def asBool : Boolean
  def asDuration: Duration

  def raw: Any

  def isEmpty : Boolean
}

class IntegerVar(l: Long) extends Variable
{
  override def dataType: DataType = DataType.Integer

  override def asDouble: Double = l

  override def asLong: Long = l

  override def asString: String = l.toString

  override def asDate: ZonedDateTime = Global.dateConversion.longToDate(l)

  override def asBool: Boolean = l > 0
  override def raw: Any = l

  override def isEmpty: Boolean = false

  override def asDuration: Duration = l.millis
}

class DoubleVar(d: Double) extends Variable
{
  override def dataType: DataType = DataType.Number

  override def asDouble: Double = d

  override def asLong: Long = d.toLong

  override def asString: String = d.toString

  override def asDate: ZonedDateTime = {
    Global.dateConversion.doubleToDate(d)
  }

  override def asBool: Boolean = d > 0

  override def raw: Any = d

  override def isEmpty: Boolean = false
  override def asDuration: Duration = d.millis
}

class StringVar(s: String) extends Variable {
  override def dataType: DataType = DataType.String


  override lazy val asDouble: Double = s.toDouble

  override lazy val asLong: Long = s.toLong

  override def asString: String = s

  override lazy val asDate: ZonedDateTime = {
    Global.dateConversion.stringToDate(s)
  }

  override lazy val asBool: Boolean = {
    s.toBoolean
  }

  override def raw: Any = s

  override def isEmpty: Boolean = s.length == 0

  override lazy val asDuration: Duration = {
    Global.dateConversion.stringToDuration(s)
  }
}

class ConversionError(v: Any) extends Exception("Fail to convert varibable:" + v)

object NullVar extends Variable {
  override def dataType: DataType = DataType.Null

  override def asDouble: Double = throw new ConversionError(null)

  override def asLong: Long = throw new ConversionError(null)

  override def asString: String = ""

  override def asDate: ZonedDateTime = throw new ConversionError(null)

  override def asBool: Boolean = false

  override def raw: Any = null

  override def isEmpty: Boolean = true

  override def asDuration: Duration = throw new ConversionError(null)
}

class DateVar(d: ZonedDateTime) extends Variable
{
  override def dataType: DataType = DataType.Date

  override def asDouble: Double = {
    Global.dateConversion.dateToDouble(d)
  }

  override def asLong: Long = {
    Global.dateConversion.dateToLong(d)
  }

  override def asString: String = {
    Global.dateConversion.dateToString(d)
  }

  override def asDate: ZonedDateTime = d

  override def asBool: Boolean = throw new ConversionError(d)

  override def raw: Any = d

  override def isEmpty: Boolean = false

  override def asDuration: Duration = throw new ConversionError(d)
}

class DurationVar(d: Duration) extends Variable {
  override def dataType: DataType = DataType.Duration

  override def asDouble: Double = d.toMillis

  override def asLong: Long = d.toMillis

  override def asString: String = d.toString

  override def asDate: ZonedDateTime = throw new ConversionError(d)

  override def asBool: Boolean = throw new ConversionError(d)

  override def asDuration: Duration = d

  override def raw: Any = d

  override def isEmpty: Boolean = false
}


object Variable{

  def apply(v: Any) : Variable = v match{
    case null | "" => NullVar
    case v: Variable => v
    case Some(v) => apply(v)
    case None => NullVar
    case c: Cell => c.variable
    case l: Long => new IntegerVar(l)
    case i: Int => new IntegerVar(i)
    case s: Short => new IntegerVar(s)
    case v: Byte => new IntegerVar(v)
    case v: Boolean => new IntegerVar(1)
    case v: Double => new DoubleVar(v)
    case v: Float => new DoubleVar(v)
    case s: String => new StringVar(s)
    case d: ZonedDateTime => new DateVar(d)
    case d: Date => new DateVar(ZonedDateTime.ofInstant(d.toInstant, ZoneId.systemDefault()))
    case d: Duration => new DurationVar(d)
    case d: java.time.Duration => new DurationVar(d.toMillis.millis)
    case any => new StringVar(any.toString)
  }



}

