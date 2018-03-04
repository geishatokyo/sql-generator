package com.geishatokyo.sqlgen.generator.sql.mysql

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.CSVLoader
import com.geishatokyo.sqlgen.meta.{ColumnMeta, ExportStrategy, Metadata, SheetMeta}
import com.geishatokyo.sqlgen.validator.MetadataValidator
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MySQLQueryGeneratorTest extends FlatSpec with Matchers {


  it should "generate insert query" in {

    implicit val meta = Metadata("MySQL",List(
      SheetMeta("User", List(
        ColumnMeta("id"),
        ColumnMeta("nickname"),
        ColumnMeta("age"),
        ColumnMeta("loginTime")
      ))
    ))

    val generator = new MySQLQueryGenerator()


    val wb = new CSVLoader().loadFromString("User",
      """id,nickname,age,loginTime
        |1,hoge,23,2017/01/01
        |2,fuga,26,2012/01/02 00:01:00
      """.stripMargin)


    val sql = generator.createInsertSQL(wb("User").rows(0))

    println(sql)

    assert(sql.contains("INSERT INTO"))

  }

}
