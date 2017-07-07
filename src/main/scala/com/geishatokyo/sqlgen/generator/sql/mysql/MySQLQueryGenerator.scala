package com.geishatokyo.sqlgen.generator.sql.mysql

import java.time.format.DateTimeFormatter

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core._
import com.geishatokyo.sqlgen.generator.sql.SQLQueryGenerator
import com.geishatokyo.sqlgen.meta.{ColumnMeta, Metadata}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MySQLQueryGenerator(protected val throwExceptionWhenMetaNotFound: Boolean = false) extends SQLQueryGenerator  {



  val format = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")

  /**
    * @param cell
    * @param className
    * @return
    */
  def toValue(cell: Cell, className: String): String = {
    className match{
      case Metadata.AutoClass => {
        cell.dataType match{
          case DataType.Integer => cell.asLong.toString
          case DataType.Number => cell.asDouble.toString
          case DataType.Bool => cell.asBool.toString
          case DataType.Date => escape(format.format(cell.asDate))
          case DataType.Duration => throw SQLGenException.atCell(cell, "Not supported data type:" + cell.dataType)
          case DataType.Null => "NULL"
          case _ => escape(cell.asString)
        }
      }
      case MySQLColumnKind.Integer => {
        cell.asLong.toString
      }
      case MySQLColumnKind.Number => {
        cell.asDouble.toString
      }
      case MySQLColumnKind.Date => {
        format.format(cell.asDate)
      }
      case MySQLColumnKind.String => {
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

    val assign = fieldAndValues.filter(fv => !ids.contains(fv._1)).
      map(fv => s"${fv._1}=${fv._2}").mkString(",")

    s"INSERT INTO ${tableName} (${fieldAndValues.map(_._1).mkString(",")}) VALUES (${fieldAndValues.map(_._2).mkString(",")}) " +
      s"ON DUPLICATE KEY UPDATE ${assign};"
  }

  override protected def createDelete(tableName: String, ids: List[String], fieldAndValues: Seq[(String, String)]): String = {


    val deleteCondition = ids.map(id => fieldAndValues.find(_._1 == id).get).
      map(f => {
      s"${f._1}=${f._2}"
    }).mkString(" and ")


    s"DELETE FROM ${tableName} WHERE ${deleteCondition};"
  }
}
