package com.geishatokyo.sqlgen.core

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import scala.concurrent.duration._
import scala.util.Try

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
  override def dataType: DataType = {
    if(tDuration.isSuccess) DataType.Duration
    else if(tDate.isSuccess) DataType.Date
    else if(tLong.isSuccess) DataType.Integer
    else if(tBool.isSuccess) DataType.Bool
    else if(tDoubnle.isSuccess) DataType.Number
    else DataType.String
  }

  private var double: Option[Try[Double]] = None
  private def tDoubnle: Try[Double] = double match {
    case Some(t) => t
    case None => {
      val t = Try{s.toDouble}
      double= Some(t)
      t
    }
  }
  private var date: Option[Try[ZonedDateTime]] = None
  private def tDate: Try[ZonedDateTime] = date match {
    case Some(t) => t
    case None => {
      val t = Try{Global.dateConversion.stringToDate(s)}
      date = Some(t)
      t
    }
  }
  private var long: Option[Try[Long]] = None
  private def tLong: Try[Long] = long match {
    case Some(t) => t
    case None => {
      val t = Try{s.toLong}
      long = Some(t)
      t
    }
  }

  private var duration: Option[Try[Duration]] = None
  private def tDuration: Try[Duration] = duration match {
    case Some(t) => t
    case None => {
      val t = Try{Global.dateConversion.stringToDuration(s)}
      duration = Some(t)
      t
    }
  }
  private var bool: Option[Try[Boolean]] = None
  private def tBool: Try[Boolean] = bool match {
    case Some(t) => t
    case None => {
      val t = Try{s.toBoolean}
      bool = Some(t)
      t
    }
  }



  override lazy val asDouble: Double = tDoubnle orElse tBool.map(b => if(b) 1.0 else 0.0) get

  override lazy val asLong: Long = tLong orElse tBool.map(b => if(b) 1L else 0L) get

  override def asString: String = s

  override lazy val asDate: ZonedDateTime = tDate.get

  override lazy val asBool: Boolean = tBool orElse tLong.map(_ == 1) get

  override def raw: Any = s

  override def isEmpty: Boolean = s.length == 0

  override lazy val asDuration: Duration = tDuration.get
}

class ConversionError(v: Any) extends Exception("Fail to convert varibable:" + v)

object NullVar extends Variable {
  override def dataType: DataType = DataType.Null

  override def asDouble: Double = 0

  override def asLong: Long = 0

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

