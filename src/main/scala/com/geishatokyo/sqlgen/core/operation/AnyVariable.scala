package com.geishatokyo.sqlgen.core.operation

import java.time.ZonedDateTime

import com.geishatokyo.sqlgen.core.conversion.DateConversion

/**
  * Created by takezoux2 on 2017/06/10.
  */
class AnyVariable(v: Any, dateConversion: DateConversion) extends Variable {
  override def asDouble: Double = {
    asString.toDouble
  }

  override def asString: String = {
    if(v == null) "null"
    else v.toString
  }

  override def asDate: ZonedDateTime = {
    dateConversion.stringToDate(asString)
  }

  override def raw: Any = v
}
