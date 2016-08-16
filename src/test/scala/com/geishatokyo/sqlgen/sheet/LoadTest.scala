package com.geishatokyo.sqlgen.sheet

import org.scalatest.{Matchers, FlatSpec}
import com.geishatokyo.sqlgen.project.input.XLSLoader
import java.util.Date

/**
 *
 * User: takeshita
 * DateTime: 13/10/15 12:44
 */
class LoadTest extends FlatSpec with Matchers{


  "XLSLoader" should
    "load expression" in {

      val s = XLSLoader.load(getClass.getClassLoader.getResourceAsStream("LoadTest.xls"))

      println(s.sheets(0).row(0))

      val row = s.sheets(0).row(0)

      println(row("time").value + "  : " + row("time").value.getClass)
      val d = row("time").asString
      println(d + " --- " + row("time").asDate + " -- " + row.header("time"))

      assert(row("sum").asInt == (row("price").asInt * row("count").asInt))




    }


}
