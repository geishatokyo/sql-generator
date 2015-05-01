package com.geishatokyo.sqlgen.sheet

import org.scalatest.{FlatSpec, Matchers}


/**
 *
 * User: takeshita
 * Create: 12/07/12 17:39
 */

class SheetTest extends FlatSpec with Matchers  {

  "Sheet " should
    "add column" in {
      val sheet = new Sheet("TestSheet")

      sheet.addColumns("Column1","Column2","Column3")

      sheet.addEmptyRow()
      sheet.addRow(List("a","b","c"))

      sheet(0,0) = "row0-0"
      sheet(0,1) = "row0-1"
      sheet(0,2) = "row0-2"

      sheet(1,2) = "Override"

      println(sheet)

      assert(sheet(1,0) == "a")

      assert(sheet(1,2) == "Override")
    }


}
