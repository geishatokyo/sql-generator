package com.geishatokyo.sqlgen.process.converter.sql

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.generator.sql.{QueryType, SQLQueryGenerator}
import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata, TypeSafeConfigMetaLoader}
import com.geishatokyo.sqlgen.process.converter.{MetadataImportProc}
import com.geishatokyo.sqlgen.process._
import com.geishatokyo.sqlgen.validator.MetadataValidator

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait SQLConverterProc extends ConverterProc[String] with MetadataValidator{

  def dbType : String
  def queryType: QueryType
  def createSqlQueryGenerator(c: Context): SQLQueryGenerator

  def forInsert: SQLConverterProc = forType(QueryType.Insert)
  def forReplace: SQLConverterProc = forType(QueryType.Replace)
  def forDelete: SQLConverterProc = forType(QueryType.Delete)
  def forType(queryType: QueryType): SQLConverterProc

  def defaultMetaFilePath = {
    s"conf/${dbType}.meta.conf"
  }


  def getName(wb: Workbook): String = {
    s"${dbType}-${wb.name}-${queryType}.sql"
  }

  override def dataKey: Key[MultiData[String]] = Key(s"result.sql.${dbType}.${queryType}")

  override def convert(c: Context): MultiData[String] = {

    val sqlQueryGenerator = createSqlQueryGenerator(c)

    val builder = new StringBuilder()
    def append(m: String) = builder.append(m + "\n")

    val wb = applyMetadata(c.workbook)

    append(sqlQueryGenerator.toLineComment(s"Workbook:${wb.name}"))
    wb.sheets.filterNot(_.isIgnore).foreach(sheet => {
      append(sqlQueryGenerator.toLineComment("Table:" + sheet.name))
      sheet.rows.foreach(row => {
        append(sqlQueryGenerator.toSQL(queryType, row))
      })

    })


    MultiData(StringData(
      getName(wb),
      builder.toString()
    ))
  }
}
