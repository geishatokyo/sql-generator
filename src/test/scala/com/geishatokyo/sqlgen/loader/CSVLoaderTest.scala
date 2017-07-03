package com.geishatokyo.sqlgen.loader

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/06/14.
  */
class CSVLoaderTest extends FlatSpec with Matchers{

  it should "load csv" in {

    val source = StringListSource("Workbook",List(
      """#@Sheet User
        |id,username,message
        |1,tom,"escape "",""
        |kaigyo"
        |2,bob,hoge
        |
        |#Skip comment
        |#@Sheet Hero
        |
        |id,name
        |1,aaa
        |2,bbb
        |3,bbb
        |4,bbb
      """.stripMargin
    ))

    val loader = new CSVLoader(source)

    val wb = loader.load()

    assert(wb.name == "Workbook")
    assert(wb.sheets.size == 2)
    assert(wb("User").columns.size == 3)
    assert(wb("User").rows.size == 2)
    assert(wb("Hero").columns.size == 2)
    assert(wb("Hero").rows.size == 4)
  }



}
