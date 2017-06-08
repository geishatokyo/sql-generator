package com.geishatokyo.sqlgen.core.operation

import java.time.{ZoneId, ZonedDateTime}
import java.util.Date

import com.geishatokyo.sqlgen.core.conversion.DateConversion

/**
  * Created by takezoux2 on 2017/06/10.
  */
class DateVariable(d: ZonedDateTime, dateConversion: DateConversion) extends Variable {


  override def asDouble: Double = {
    dateConversion.dateToDouble(d)
  }

  override def asString: String = {
    dateConversion.dateToString(d)
  }

  override def asDate: ZonedDateTime = d

  override def raw: Any = d
}

object DateVariable{
  def apply(d: Date, dateConversion: DateConversion) = {
    val ins = d.toInstant
    new DateVariable(ZonedDateTime.ofInstant(ins, ZoneId.systemDefault()), dateConversion)
  }
}