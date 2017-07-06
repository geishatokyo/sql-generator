package com.geishatokyo.sqlgen.process.converter.code

import com.geishatokyo.sqlgen.generator.code.csharp.CSharpCodeGenerator
import com.geishatokyo.sqlgen.process.converter.UsingMetaFile
import com.geishatokyo.sqlgen.process._

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait CSharpCodeConverterProc extends ConverterProc[String] with UsingMetaFile{


  def codeType = "csharp"

  override def defaultMetaFilePath: String = s"conf/${codeType}.meta.conf"

  override def metadataKey = Key(s"meta.code.${codeType}")

  override def dataKey = Key(s"result.code.${codeType}")

  val generator = new CSharpCodeGenerator()

  override def convert(c: Context): MultiData[String] = {

    val gen = this.generator
    implicit val m = this.getMetadata(c)

    val codeLines = c.workbook.sheets.map(sheet => {
      val lines = sheet.rows.map(row => {
        gen.createStatement(row)
      }).toList
      (sheet.name,lines)
    }).toList

    val className = getClassName(c)
    MultiData(StringData(className + ".cs", generateCode(c, codeLines)))

  }

  def getClassName(c: Context): String

  def generateCode(c: Context,list: List[(String,List[String])]): String



}
