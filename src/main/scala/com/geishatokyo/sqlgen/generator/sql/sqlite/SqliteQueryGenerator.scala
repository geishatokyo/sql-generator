package com.geishatokyo.sqlgen.generator.sql.sqlite

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.{Cell, DataType}
import com.geishatokyo.sqlgen.generator.sql.SQLQueryGenerator
import com.geishatokyo.sqlgen.generator.sql.mysql.MySQLColumnKind
import com.geishatokyo.sqlgen.meta.Metadata

/**
  * Created by takezoux2 on 2017/07/06.
  */
class SqliteQueryGenerator(val throwExceptionWhenMetaNotFound: Boolean = false) extends SQLQueryGenerator{


  override def toValue(cell: Cell, className: Option[String]): String = {
    className match{
      case None | Some(Metadata.AutoClass) => {
        cell.dataType match{
          case DataType.Integer => cell.asLong.toString
          case DataType.Number => cell.asDouble.toString
          case DataType.Bool => cell.asBool.toString
          case DataType.Date => cell.asDate.toInstant.toEpochMilli.toString // Date型が無いので、unixTimeで保存
          case DataType.Duration => throw SQLGenException.atCell(cell, "Not supported data type:" + cell.dataType)
          case DataType.Null => "NULL"
          case _ => escape(cell.asString)
        }
      }
      case Some(SqliteColumnKind.Integer) => {
        cell.asLong.toString
      }
      case Some(SqliteColumnKind.Date) => {
        cell.asDate.toInstant.toEpochMilli.toString
      }
      case Some(SqliteColumnKind.Number) => {
        cell.asDouble.toString
      }
      case Some(SqliteColumnKind.String) => {
        escape(cell.asString)
      }
      case _ => {
        throw SQLGenException.atCell(cell,"Not supported class type:" + className)
      }
    }
  }

  override protected def createInsert(tableName: String, fieldAndValues: Seq[(String, String)]): String = {
    s"INSERT INTO ${tableName} (${fieldAndValues.map(_._1).mkString(",")}) VALUES (${fieldAndValues.map(_._2).mkString(",")});"
  }

  override protected def createReplace(tableName: String, ids: List[String], fieldAndValues: Seq[(String, String)]): String = {
    s"REPLACE ${tableName} (${fieldAndValues.map(_._1).mkString(",")}) VALUES (${fieldAndValues.map(_._2).mkString(",")});"
  }

  override protected def createDelete(tableName: String, ids: List[String], fieldAndValues: Seq[(String, String)]): String = {
    val deleteCondition = ids.map(id => fieldAndValues.find(_._1 == id).get).
      map(f => {
        s"${f._1}=${f._2}"
      }).mkString(" and ")
    s"DELETE FROM ${tableName} WHERE ${deleteCondition};"
  }
}
