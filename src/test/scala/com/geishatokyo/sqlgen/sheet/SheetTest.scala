package com.geishatokyo.sqlgen.sheet

import org.specs2.mutable.SpecificationWithJUnit

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:39
 */

class SheetTest extends SpecificationWithJUnit {

  "Sheet " should{
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

      sheet(1,0) must_== "a"

      sheet(1,2) must_== "Override"
      sheet.cellAt(1,2).initialValue must_== "c"
    }
  }

}
