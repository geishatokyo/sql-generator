package com.geishatokyo.sqlgen.core.conversion


import java.time.Month

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/12/01.
  */
class DateConversionTest extends FlatSpec with Matchers {

  "date time" should "be converted" in {

    assertS2D("2017-01-2 22:44:20.2")
    assertS2D("2017/01/2 22:44:20.2")
  }

  def assertS2D(y20170102_224420200: String) = {

    val d = DefaultDateConversion.stringToDate(y20170102_224420200)
    assert(d.getYear == 2017)
    assert(d.getMonth == Month.JANUARY)
    assert(d.getDayOfMonth == 2)
    assert(d.getHour == 22)
    assert(d.getMinute == 44)
    assert(d.getSecond == 20)

  }

}
