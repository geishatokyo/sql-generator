package com.geishatokyo.sqlgen.process.converter.sql

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.core.Sheet
import com.geishatokyo.sqlgen.generator.sql.sqlite.SqliteQueryGenerator
import com.geishatokyo.sqlgen.generator.sql.{QueryType, SQLQueryGenerator}
import com.geishatokyo.sqlgen.meta.Metadata
import com.geishatokyo.sqlgen.process.{Context, Key}

/**
 *
 * User: takeshita
 * Create: 12/07/11 23:48
 */

class SqliteConverterProc(val queryType: QueryType) extends SQLConverterProc {

  override def dbType: String = "sqlite"

  override def metadataKey: Key[Metadata] = SqliteConverterProc.MetadataKey

  override def createSqlQueryGenerator(c: Context): SQLQueryGenerator = {
    new SqliteQueryGenerator(false)
  }

  override def forType(queryType: QueryType): SQLConverterProc = {
    new SqliteConverterProc(queryType)
  }
}

object SqliteConverterProc {
  val MetadataKey = Key[Metadata]("metadata.sql.sqlite")
}