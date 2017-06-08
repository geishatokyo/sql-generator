package com.geishatokyo.sqlgen.core.operation
import java.time.ZonedDateTime

import com.geishatokyo.sqlgen.core.Cell
import com.geishatokyo.sqlgen.core.conversion.DateConversion

import scala.util.Try

/**
  * Created by takezoux2 on 2017/06/09.
  */
class StringVariable(s: String, dateConversion: DateConversion) extends Variable{ self =>


  override def asDouble: Double = {
    s.toDouble
  }

  override def asString: String = s

  override def asDate: ZonedDateTime = {
    dateConversion.stringToDate(s)
  }

  override def raw: Any = s




}
