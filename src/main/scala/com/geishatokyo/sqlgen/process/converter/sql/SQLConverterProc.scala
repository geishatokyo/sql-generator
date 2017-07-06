package com.geishatokyo.sqlgen.process.converter.sql

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.generator.sql.{QueryType, SQLQueryGenerator}
import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata, TypeSafeConfigMetaLoader}
import com.geishatokyo.sqlgen.process.converter.{MetadataImportProc, SetMetadataProc}
import com.geishatokyo.sqlgen.process._

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait SQLConverterProc extends ConverterProc[String] {

  def dbType : String
  def metadataKey : String
  def queryType: QueryType
  def createSqlQueryGenerator(c: Context): SQLQueryGenerator


  def metaLoader: MetaLoader = new TypeSafeConfigMetaLoader()

  def forInsert: SQLConverterProc = forType(QueryType.Insert)
  def forReplace: SQLConverterProc = forType(QueryType.Replace)
  def forDelete: SQLConverterProc = forType(QueryType.Delete)
  def forType(queryType: QueryType): SQLConverterProc

  private var metaProc : Option[Proc] = None
  override def thisProc: Proc = {
    metaProc match{
      case Some(p) => p >> this
      case None => this
    }
  }

  def withMeta(meta: Metadata) = {
    this.metaProc = Some(new SetMetadataProc(metadataKey, meta))
    this
  }
  def withMetaFile(path: String) = {
    this.metaProc = Some(new MetadataImportProc(path, metadataKey, metaLoader) )
    this
  }

  def getMetadata(c: Context): Metadata = {
    if(c.has(metadataKey)) {
      c(metadataKey)
    } else {
      Metadata.Empty
    }
  }

  def getName(wb: Workbook): String = {
    s"${dbType}-${wb.name}-${queryType}.sql"
  }

  override def dataKey: String = s"result.sql.${dbType}.${queryType}"

  override def convert(c: Context): MultiData[String] = {

    val sqlQueryGenerator = createSqlQueryGenerator(c)
    implicit val metadata = getMetadata(c)

    val wb = c.workbook

    val builder = new StringBuilder()
    def append(m: String) = builder.append(m + "\n")

    append(sqlQueryGenerator.toLineComment(s"Workbook:${wb.name}"))
    wb.sheets.foreach(sheet => {
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
