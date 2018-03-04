package com.geishatokyo.sqlgen.generator.sql

import java.time.format.DateTimeFormatter

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.{Cell, Column, Row, Sheet}
import com.geishatokyo.sqlgen.meta.Metadata

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait SQLQueryGenerator {

  protected def throwExceptionWhenMetaNotFound: Boolean

  def toSQL(queryType: QueryType, row: Row) = {
    queryType match {
      case QueryType.Insert => createInsertSQL(row)
      case QueryType.Replace => createReplaceSQL(row)
      case QueryType.Delete => createDeleteSQL(row)
    }
  }


  protected def getIds(row: Row): List[String] = {
    val ids = row.parent.ids.map(_.name).toList
    if(ids.size == 0) {
      println(row.parent.headers.map(_.name).toList)
      if(row.parent.hasColumn("id")) {
        List("id")
      } else {
        Nil
      }
    } else {
      ids
    }
  }
  protected def getFieldNameAndValue(row: Row): Seq[(String,String)] = {
    row.cells.filterNot(_.header.isIgnore).map(c => {
      (c.header.name, toValue(c, c.header.columnType))
    })
  }

  def toValue(cell: Cell, className: Option[String]): String

  def toLineComment(m: String): String = "-- " + m

  protected def escape(_s: String) = {
    val s = _s.replace("\\","\\\\").replace("\n","\\n")
    if(s.contains("\"")) {
      "'" + s.replace("'","\\'") + "'"
    } else {
      '"' + s + '"'
    }
  }



  def createInsertSQL(row: Row) : String= {

    val name = row.parent.name
    val fieldAndValues = getFieldNameAndValue(row)
    createInsert(name, fieldAndValues)
  }

  protected def createInsert(tableName: String, fieldAndValues: Seq[(String,String)]): String

  def createReplaceSQL(row: Row) = {
    val ids = getIds(row)
    if(ids.size == 0) {
      throw SQLGenException.atSheet(row.parent, "No ids")
    }
    val name = row.parent.name
    val fieldAndValues = getFieldNameAndValue(row)
    ids.find(id => !fieldAndValues.exists(_._1.toUpperCase == id.toUpperCase)) match {
      case Some(idNotExists) => {
        throw SQLGenException.atSheet(row.parent, s"Wrong id name:${idNotExists}")
      }
      case None =>
    }

    createReplace(name, ids, fieldAndValues)
  }

  protected def createReplace(tableName: String, ids: List[String], fieldAndValues: Seq[(String,String)]): String


  def createDeleteSQL(row: Row) = {
    val ids = getIds(row)
    if(ids.size == 0) {
      throw SQLGenException.atSheet(row.parent, "No ids")
    }

    val name = row.parent.name
    val fieldAndValues = getFieldNameAndValue(row)
    ids.find(id => !fieldAndValues.exists(_._1.toUpperCase == id.toUpperCase)) match {
      case Some(idNotExists) => {
        throw SQLGenException.atSheet(row.parent, s"Wrong id name:${idNotExists}")
      }
      case None =>
    }

    createDelete(name, ids, fieldAndValues)
  }

  protected def createDelete(tableName: String, ids: List[String], fieldAndValues: Seq[(String,String)]): String


}
