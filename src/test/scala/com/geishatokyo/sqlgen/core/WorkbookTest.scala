package com.geishatokyo.sqlgen.core

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/05/27.
  */
class WorkbookTest extends FlatSpec with Matchers  {


  it should "add" in {

    val workbook = new Workbook("MyWorkbook")
    val sheet = workbook.addSheet("Sheet1")
    sheet.addHeader("id")
    sheet.addRow(1)

  }




}
