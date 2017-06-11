package com.geishatokyo.sqlgen.core.operation

import java.time.{Instant, ZoneId, ZonedDateTime}

/**
  * Created by takezoux2 on 2017/06/11.
  */
class NullVariable extends Variable{
  override def asDouble: Double = 0

  override def asString: String = ""

  override def asDate: ZonedDateTime = ZonedDateTime.ofInstant(
    Instant.EPOCH,
    ZoneId.systemDefault()
  )

  override def raw: Any = null

  override def isEmpty: Boolean = true
}
