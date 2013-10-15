package com.geishatokyo.sqlgen.sheet

import org.specs2.mutable.Specification
import com.geishatokyo.sqlgen.project2.input.XLSLoader

/**
 *
 * User: takeshita
 * DateTime: 13/10/15 12:44
 */
class LoadTest extends Specification{


  "XLSLoader" should{
    "load expression" in {

      val s = XLSLoader.load(getClass.getClassLoader.getResourceAsStream("LoadTest.xls"))

      println(s.sheets(0).row(0))

      val row = s.sheets(0).row(0)
      row("sum").value.toDouble === (row("price").value.toLong * row("count").value.toInt)

    }
  }

}
