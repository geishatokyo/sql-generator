package com.geishatokyo.sqlgen.meta

import com.typesafe.config.ConfigFactory
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class TypeSafeConfigMetaLoaderTest extends FlatSpec with Matchers {

  it should "load correct config" in {

    val config = ConfigFactory.parseString(
      """
        |sheets : [{
        |  name: User
        |  columns: [{
        |    name: id
        |    type: Long
        |  },{
        |    name: nickname
        |    type: String
        |  }]
        |}, {
        |  name: Hoge
        |  columns: [{
        |    name: id
        |    type: Int
        |  },{
        |    name: name
        |  }]
        |}]
        |
        |
      """.stripMargin)

    val loader = new TypeSafeConfigMetaLoader()

    val metadata = loader.loadConfig(config)

    println(metadata)
    assert(metadata.sheetMetas.size == 2)

    assert(metadata == Metadata(List(
      SheetMeta("User",List(
        ColumnMeta("id"),
        ColumnMeta("nickname")
      )),
      SheetMeta("Hoge",List(
        ColumnMeta("id"),
        ColumnMeta("name")
      ))
    )))


  }

}
