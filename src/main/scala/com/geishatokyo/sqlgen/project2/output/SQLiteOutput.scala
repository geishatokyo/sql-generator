package com.geishatokyo.sqlgen.project2.output

import com.geishatokyo.sqlgen.project2.Output
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import com.geishatokyo.sqlgen.sheet.convert.{SQLiteConverter, MySQLConverter}
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.util.FileUtil

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:59
 */
class SQLiteOutput extends Output {

  val logger = Logger.logger
  val sqliteConverter = new SQLiteConverter

  def write(context: Context, w: Workbook) {

    writeSql(context,w,"insert",sqliteConverter.toInsertSQL _)
    writeSql(context,w,"update",s => {
      sqliteConverter.toUpdateSQL(s,s.ids.map(_.name.value))
    })
    writeSql(context,w,"delete",s => {
      sqliteConverter.toDeleteSQL(s,s.ids.map(_.name.value))
    })

  }
  def writeInserts(context: Context, w: Workbook) {
  }
  private def writeSql(context: Context, w: Workbook,action : String, convert : Sheet => String) {
    val sqls = w.sheets.filter( !_.ignore).map(convert)
    if (sqls.size > 0){
      val path = FileUtil.joinPath(context.workingDir,action + "_" + w.name + ".sqlite.sql")
      logger.log("Save " + action + " sql to %s".format(path))
      FileUtil.saveTo(path,sqls)
    }
  }
}
