package com.geishatokyo.sqlgen.core

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/07/04.
  */
class CellOperationTest extends FlatSpec with Matchers  {


  "plus" should "work with doublable values" in {
    val w = new Workbook("test")
    val s = w.addSheet("sheet")

    s.addHeader("v")

    s.addRow(1L)
    s.addRow("23")
    s.addRow(21.3)

    assert( (s(0,0) + s(0,1)) == 24)
    assert( s(0,0) + s(0,2) == 22.3)
  }

  "plus" should "work with databale and durationable value" in {
    val w = new Workbook("test")
    val s = w.addSheet("sheet")

    s.addHeader("v")
    s.addRow("2015/01/02 00:00:00")
    s.addRow("3 day")

    assert( s(0,0) + s(0,1) == Variable("2015/01/05").asDate)
  }


}
