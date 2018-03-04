package com.geishatokyo.sqlgen.process.converter.code

import com.geishatokyo.sqlgen.generator.code.csharp.CSharpCodeGenerator
import com.geishatokyo.sqlgen.process._
import com.geishatokyo.sqlgen.validator.MetadataValidator

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait CSharpCodeConverterProc extends ConverterProc[String] with MetadataValidator{

  def codeType = "csharp"

  override def dataKey = Key(s"result.code.${codeType}")


  override def metadataName: String = "CSharp"

  val generator = new CSharpCodeGenerator()

  override def convert(c: Context): MultiData[String] = {

    val gen = this.generator

    val wb = applyMetadata(c.workbook)

    val codeLines = wb.sheets.filterNot(_.isIgnore).map(sheet => {
      val lines = sheet.rows.map(row => {
        gen.createStatement(row)
      }).toList
      (sheet.name,lines)
    })

    val className = getClassName(c)
    MultiData(StringData(className + ".cs", generateCode(c, codeLines)))

  }

  def getClassName(c: Context): String

  def generateCode(c: Context,list: List[(String,List[String])]): String



}
