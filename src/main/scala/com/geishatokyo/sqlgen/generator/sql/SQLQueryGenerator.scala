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

  def toSQL(queryType: QueryType, row: Row)(implicit metadata: Metadata) = {
    queryType match {
      case QueryType.Insert => createInsertSQL(row)
      case QueryType.Replace => createReplaceSQL(row)
      case QueryType.Delete => createDeleteSQL(row)
    }
  }

  protected def getTableName(row: Row)(implicit metadata: Metadata) = {
    metadata.getSheetMeta(row.parent.name) match{
      case Some(metadata) => metadata.className
      case None => row.parent.name
    }
  }

  protected def getColumnMeta(c: Column)(implicit metadata: Metadata) = {
    metadata.getSheetMeta(c.parent.name).flatMap(sm => {
      sm.getColumnMeta(c.header.name)
    })
  }

  protected def getIds(row: Row)(implicit metadata: Metadata): List[String] = {
    metadata.getSheetMeta(row.parent.name) match {
      case Some(metadata) => {
        if(metadata.primaryIndex.size > 0) {
          metadata.primaryIndex
        } else {
          row.parent.headers.filter(h => !h.isIgnore && h.isId).map(_.name).toList
        }
      }
      case None => {
        val ids = row.parent.headers.filter(h => !h.isIgnore && h.isId).map(_.name).toList

        if(ids.size == 0) {
          if(row.parent.hasColumn("id")) {
            List("id")
          } else {
            Nil
          }
        } else ids
      }
    }
  }
  protected def getFieldNameAndValue(row: Row)(implicit metadata: Metadata): Seq[(String,String)] = {

    if(!metadata.getSheetMeta(row.parent.name).isDefined) {
      if(throwExceptionWhenMetaNotFound) {
        throw SQLGenException.atSheet(row.parent,s"Metadata for Sheet:${row.parent.name} not found")
      }
      row.cells.filter(!_.header.isIgnore).map(c => {
        (c.header.name, toValue(c, Metadata.AutoClass))
      })
    } else {
      row.cells.flatMap(c => {
        if(c.header.isIgnore) None
        else getColumnMeta(c.column) match{
          case Some(m) => {
            if(m.isIgnore ) None
            else Some((c.header.name, toValue(c, m.className)))
          }
          case None => None
        }
      }).toList
    }


  }

  def toValue(cell: Cell, className: String): String

  def toLineComment(m: String): String = "-- " + m

  protected def escape(_s: String) = {
    val s = _s.replace("\n","\\n")
    if(s.contains("\"")) {
      "'" + s.replace("'","\\'") + "'"
    } else {
      '"' + s + '"'
    }
  }

  private var checkResult = Map[String,Option[String]]()
  protected def checkAllFieldsExists(sheet: Sheet)(implicit metadata: Metadata) = checkResult.get(sheet.address) match{
    case Some(Some(error)) => throw SQLGenException.atSheet(sheet,error)
    case Some(None) =>
    case None => {
      metadata.getSheetMeta(sheet.name) match {
        case Some(meta) => {
          val fields = sheet.headers.map(_.name).toSet

          meta.columnMetas.find(c => !fields.contains(c.name)) match {
            case Some(column) => {
              val message = s"Field:${column.name} not exists"
              checkResult += (sheet.address -> Some(message))
              throw SQLGenException.atSheet(sheet,message)
            }
            case None => {
              checkResult += (sheet.address -> None)
            }
          }
        }
        case None => {
          if (throwExceptionWhenMetaNotFound) {
            val message =  s"Metadata for Sheet:${sheet.name} not found"
            checkResult += (sheet.address -> Some(message))
            throw SQLGenException.atSheet(sheet, message)
          }else {
            checkResult += (sheet.address -> None)
          }

        }
      }
    }
  }


  def createInsertSQL(row: Row)(implicit metadata: Metadata) : String= {
    checkAllFieldsExists(row.parent)

    val name = getTableName(row)
    val fieldAndValues = getFieldNameAndValue(row)
    createInsert(name, fieldAndValues)
  }

  protected def createInsert(tableName: String, fieldAndValues: Seq[(String,String)]): String

  def createReplaceSQL(row: Row)(implicit metadata: Metadata) = {
    checkAllFieldsExists(row.parent)
    val ids = getIds(row)
    if(ids.size == 0) {
      throw SQLGenException.atSheet(row.parent, "No ids")
    }
    val name = getTableName(row)
    val fieldAndValues = getFieldNameAndValue(row)
    ids.find(id => !fieldAndValues.exists(_._1 == id)) match {
      case Some(idNotExists) => {
        throw SQLGenException.atSheet(row.parent, s"Wrong id name:${idNotExists}")
      }
      case None =>
    }

    createReplace(name, ids, fieldAndValues)
  }

  protected def createReplace(tableName: String, ids: List[String], fieldAndValues: Seq[(String,String)]): String


  def createDeleteSQL(row: Row)(implicit metadata: Metadata) = {
    checkAllFieldsExists(row.parent)
    val ids = getIds(row)
    if(ids.size == 0) {
      throw SQLGenException.atSheet(row.parent, "No ids")
    }

    val name = getTableName(row)
    val fieldAndValues = getFieldNameAndValue(row)
    ids.find(id => !fieldAndValues.exists(_._1 == id)) match {
      case Some(idNotExists) => {
        throw SQLGenException.atSheet(row.parent, s"Wrong id name:${idNotExists}")
      }
      case None =>
    }

    createDelete(name, ids, fieldAndValues)
  }

  protected def createDelete(tableName: String, ids: List[String], fieldAndValues: Seq[(String,String)]): String


}
