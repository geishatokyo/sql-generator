package com.geishatokyo.sqlgen.validator

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.loader.CSVLoader
import com.geishatokyo.sqlgen.meta.{ColumnMeta, ExportStrategy, Metadata, SheetMeta}
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MetadataValidatorTest extends FlatSpec with Matchers {


  class MyValidator extends MetadataValidator {
    override def metadataName: String = "MySQL"
  }

  it should "throw exception if field not found" in {

    val meta = Metadata("MySQL",List(
      SheetMeta("User", List(
        ColumnMeta("id"),
        ColumnMeta("nickname"),
        ColumnMeta("age"),
        ColumnMeta("loginTime")
      ))
    ))

    meta.columnNotFoundExportStrategy = ExportStrategy.ThrowException

    // loginTimeフィールドが欠落している
    val wb = new CSVLoader().loadFromString("User",
      """id,nickname,age
        |1,hoge,23
        |2,fuga,26
      """.stripMargin)

    wb.addMetadata(meta)

    assertThrows[SQLGenException] {
      new MyValidator().applyMetadata(wb)
    }
  }

}
