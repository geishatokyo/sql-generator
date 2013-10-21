package com.geishatokyo.sqlgen.sheet.convert

import com.geishatokyo.sqlgen.sheet.{CellUnit, Sheet}
import com.geishatokyo.sqlgen.logger.Logger

/**
 *
 * User: takeshita
 * Create: 12/07/11 23:48
 */

class SQLiteConverter extends SQLConverter {

  var makeTableNameToLowerCase = true

  def toInsertSQL(sheet: Sheet): String = {

    val headers = sheet.headers.withFilter(_.output_?).map(_.name.toString)
    if (sheet.rowSize == 0) return ""

    val values = sheet.foreachRow(row => {
      row.units.withFilter( cu => cu.header.output_?).map(cu => {
        asSQLString(cu.header.columnType,cu.value)
      }).mkString("(",",",")")
    })

    val tableName = if(makeTableNameToLowerCase){
      sheet.name.toString.toLowerCase
    }else{
      sheet.name.toString
    }

    values.map( v => {

      "INSERT INTO %s (%s) VALUES %s;".format(
        tableName,
        headers.mkString(","),
        v
      )
    }).mkString("\n")

  }

  def toDeleteSQL(sheet: Sheet,primaryKeys: List[String]): String = {
    if (sheet.rowSize == 0) return ""
    if(primaryKeys.size == 0) {
      Logger.log("Can't generate delete sql for " + sheet.name)
      return ""
    }


    val tableName = if(makeTableNameToLowerCase){
      sheet.name.toString.toLowerCase
    }else{
      sheet.name.toString
    }

    if (primaryKeys.size == 1){
      val pkH = sheet.header(primaryKeys(0))
      val pk = pkH.name()
      val ids = sheet.foreachRow(row => asSQLString(pkH.columnType, row(pk)))
      """DELETE FROM %s WHERE %s in %s;""".format(
        tableName,
        pk,ids.mkString("(",",",")")
      )
    }else{
      val headers = sheet.headers.withFilter( h => {
        primaryKeys.exists(pk => h.name =~= pk)
      }).map(_.name.toString)
      val values =  sheet.foreachRow(row => {
        row.units.withFilter( cu => {
          headers.contains(cu.header.name.toString)
        }).map(cu => {
          asSQLString(cu.header.columnType,cu.value)
        })
      })
      """DELETE FROM %s WHERE %s;""".format(
        tableName,
        values.map( row => headers.zip(row).map(p => {
          "(" + p._1 + " = " + p._2 + ")"
        }).mkString("("," and ",")")).mkString(" or\n")
      )
    }

  }

  def toUpdateSQL(sheet : Sheet, primaryKeys : List[String]) : String = {
    if (sheet.rowSize == 0) return ""
    if(primaryKeys.size == 0) {
      Logger.log("Can't generate update sql for " + sheet.name)
      return ""
    }
    val primaryKeySet = primaryKeys.map(_.toLowerCase).toSet
    val idHeaders = sheet.headers.withFilter( h => {
      primaryKeys.exists(pk => h.name =~= pk)
    }).map(_.name.toString)


    val tableName = if(makeTableNameToLowerCase){
      sheet.name.toString.toLowerCase
    }else{
      sheet.name.toString
    }

    sheet.foreachRow(row => {

      val setClause = row.units.filter({
        case CellUnit(h,c) => {
          h.output_? &&
          !primaryKeySet.contains(h.name.value.toLowerCase)
        }
      }).map({
        case CellUnit(h,c) => {
          h.name.toString + "=" + asSQLString(h.columnType,c)
        }
      }).mkString(",")
      val whereClause = row.units.filter({
        case CellUnit(h,c) => {
          h.output_? &&
          primaryKeySet.contains(h.name.value.toLowerCase)
        }
      }).map({
        case CellUnit(h,c) => {
          h.name.toString + "=" + asSQLString(h.columnType,c)
        }
      }).mkString(" and ")

      """UPDATE %s SET %s WHERE %s;""".format(tableName,setClause,whereClause)
    }).mkString("\n")

  }

  def toReplaceSQL(sheet: Sheet, primaryKeys: List[String]) : String = {

    val headers = sheet.headers.withFilter(_.output_?).map(_.name.toString)
    if (sheet.rowSize == 0) return ""

    val values = sheet.foreachRow(row => {
      row.units.withFilter( cu => cu.header.output_?).map(cu => {
        asSQLString(cu.header.columnType,cu.value)
      }).mkString("(",",",")")
    })


    val tableName = if(makeTableNameToLowerCase){
      sheet.name.toString.toLowerCase
    }else{
      sheet.name.toString
    }


    values.map( v => {

      "INSERT OR REPLACE INTO %s (%s) VALUES %s;".format(
        tableName,
        headers.mkString(","),
        v
      )
    }).mkString("\n")
  }

}
