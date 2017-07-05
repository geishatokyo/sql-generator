package com.geishatokyo.sqlgen.process.converter.sql

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.core.{ Sheet}

/**
 *
 * User: takeshita
 * Create: 12/07/11 23:48
 */

class SQLiteConverter extends SQLConverter {

  /*
  var makeTableNameToLowerCase = true

  def toInsertSQL(sheet: Sheet): String = {

    val headers = sheet.headers.withFilter(!_.isIgnore).map(_.name.toString)
    if (sheet.rowSize == 0) return ""

    val values = sheet.rows.map(row => {
      row.cells.withFilter( c => !c.header.isIgnore).map(cu => {
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
      val pk = pkH.name
      val ids = sheet.rows.map(row => asSQLString(pkH.columnType, row(pk)))
      """DELETE FROM %s WHERE %s in %s;""".format(
        tableName,
        pk,ids.mkString("(",",",")")
      )
    }else{
      val headers = sheet.headers.withFilter( h => {
        primaryKeys.exists(pk => h.name == pk)
      }).map(_.name.toString)
      val values =  sheet.rows.map(row => {
        row.cells.withFilter( cu => {
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
      primaryKeys.exists(pk => h.name == pk)
    }).map(_.name.toString)


    val tableName = if(makeTableNameToLowerCase){
      sheet.name.toString.toLowerCase
    }else{
      sheet.name.toString
    }

    sheet.rows.map(row => {

      val setClause = row.cells.filter(c => {
        val h = c.header
        !h.isIgnore &&
         !primaryKeySet.contains(c.header.name.toLowerCase)
      }).map(c => {
        val h = c.header
        c.header.name.toString + "=" + asSQLString(h.columnType, c)

      }).mkString(",")
      val whereClause = row.cells.filter(c => {
        val h = c.header
        !h.isIgnore &&
          primaryKeySet.contains(h.name.toLowerCase)

      }).map(c => {
        val h = c.header
        h.name.toString + "=" + asSQLString(h.columnType, c)

      }).mkString(" and ")

      """UPDATE %s SET %s WHERE %s;""".format(tableName,setClause,whereClause)
    }).mkString("\n")

  }

  def toReplaceSQL(sheet: Sheet, primaryKeys: List[String]) : String = {

    val headers = sheet.headers.withFilter(!_.isIgnore).map(_.name.toString)
    if (sheet.rowSize == 0) return ""

    val values = sheet.rows.map(row => {
      row.cells.withFilter( cu => !cu.header.isIgnore).map(cu => {
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

  override def escapeSQLString(v: String) = {
    if (v == null) "NULL"
    else{
      "'" + v.replace("'","''") + "'"
    }
  }*/
}
