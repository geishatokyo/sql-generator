package com.geishatokyo.sqlgen.load


import java.io.File

import com.geishatokyo.sqlgen.project.input.{InputStreamSource}
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by takezoux2 on 2016/08/09.
  */
class LoadXLSXTest extends FlatSpec with Matchers{

  it should "load .xlsx" in {
    val wb = new InputStreamSource(getClass.getClassLoader.getResourceAsStream("test.xlsx")).asXls().read()
    println(wb)
  }


  it should "load .xls" in {
    val wb = new InputStreamSource(getClass.getClassLoader.getResourceAsStream("test.xls")).asXls().read()
    println(wb)
  }

}
