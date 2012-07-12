package com.geishatokyo.sqlgen.sheet.convert

import com.geishatokyo.sqlgen.sheet.Sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 23:48
 */

class MySQLConverter extends SQLConverter {

  def toInsertSQL(sheet: Sheet): String = {

    val headers = sheet.headers.withFilter(_.output_?).map(_.name.toString)
    if (sheet.rowSize == 0) return ""

    val values = sheet.foreachRow(row => {
      row.units.withFilter( cu => cu.header.output_?).map(cu => {
        asSQLString(cu.header.columnType,cu.value)
      }).mkString("(",",",")")
    })

    "INSERT INTO %s (%s) VALUES\n%s;".format(
      sheet.name.toString,
      headers.mkString(","),
      values.mkString(",\n")
    )

  }

  def toDeleteSQL(sheet: Sheet,primaryKeys: List[String]): String = {
    if(primaryKeys.size == 0) return ""
    if (sheet.rowSize == 0) return ""
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
      sheet.name.toString,
      values.map( row => headers.zip(row).map(p => {
        "(" + p._1 + " = " + p._2 + ")"
      }).mkString("("," and ",")")).mkString(" or\n")
    )

  }
}
