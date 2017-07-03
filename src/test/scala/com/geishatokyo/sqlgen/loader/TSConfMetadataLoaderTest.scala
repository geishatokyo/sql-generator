package com.geishatokyo.sqlgen.loader

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/06/14.
  */
class TSConfMetadataLoaderTest extends FlatSpec with Matchers{


  it should "load conf" in {

    val md = new TypesafeConfigMetadataLoader(TSConfSource.fromString("test",
      """name: Test
        |hoge: [1,2,3]
        |
        |sheets:[{
        |  name: User
        |  columns: [{
        |    name: id
        |    isId: true
        |    type: {
        |      scala: Long,
        |      csharp: long
        |    }
        |  }]
        |},{
        |  name: Hero
        |  columns: [{
        |    name: id
        |    isId: true
        |  }]
        |}]
      """.stripMargin)).load()


    val col_user_id = md.getSheetMetadata("User").getColumnMetadata("id")

    assert(md.hoge.isDefined)
    println(md.hoge.get.getClass) // java.util.ArrayList
    assert(col_user_id.isUnique == true)


  }
}
