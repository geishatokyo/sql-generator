package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.loader.XLSLoader
import org.scalatest.{FlatSpec, Matchers}
import com.geishatokyo.sqlgen._
/**
  * Created by takezoux2 on 2017/07/05.
  */
class XLSLoaderTest extends FlatSpec with Matchers{


  it should "load xlsx" in {
    val input = getClass.getClassLoader.getResourceAsStream("test.xlsx")

    val loader = new XLSLoader()

    val w = loader.load("test",input)

    workbook(w) >> showConsole execute

  }

}
