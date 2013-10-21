package com.geishatokyo.sqlgen.sheet.convert

import com.geishatokyo.sqlgen.sheet.{ColumnType, Sheet, Cell}
import java.text.SimpleDateFormat

/**
 *
 * User: takeshita
 * Create: 12/07/11 23:46
 */

trait SQLConverter {

  def toInsertSQL(sheet: Sheet) : String

  def toDeleteSQL( sheet : Sheet , primaryKeys : List[String]) : String

  def toUpdateSQL(sheet : Sheet, primaryKeys : List[String]) : String

  def toReplaceSQL(sheet : Sheet, primaryKeys : List[String]) : String

  def asSQLString(cellType : ColumnType.Value, cell : Cell) = {
    cellType match{
      case ColumnType.Integer => cell.asLong.toString
      case ColumnType.Double => cell.asDouble.toString
      case ColumnType.Date => {
        val d = cell.asDate
        if (d == null){
          "NULL"
        }else{
          escapeSQLString(
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d)
          )
        }
      }
      case ColumnType.String => escapeSQLString(cell.asString)
      case ColumnType.Any => escapeSQLString(cell.asString)
    }
  }

  def escapeSQLString(v : String) = {
    if (v == null) "NULL"
    else{
      "'" + v.replace("\\","\\\\").replace("'","\\'").replace("\n","\\n").replace("\r","") + "'"
    }
  }

}
