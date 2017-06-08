package com.geishatokyo.sqlgen.core.operation
import java.time.{Instant, ZoneId, ZonedDateTime}

import com.geishatokyo.sqlgen.core.Cell
import com.geishatokyo.sqlgen.core.conversion.DateConversion

import scala.util.{Success, Try}

/**
  * Created by takezoux2 on 2017/06/08.
  */
class DoubleVariable(v: Double, dateConversion: DateConversion) extends Variable{ self =>

  override def asDouble: Double = v

  override def asDate: ZonedDateTime = {
    dateConversion.doubleToDate(v)
  }
  override def raw: Any = v

  override def asString: String = if(v % 1.0 == 0) v.toLong.toString else v.toString
}
