package com.geishatokyo.sqlgen.project2.output

import com.geishatokyo.sqlgen.project2.Output
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import com.geishatokyo.sqlgen.sheet.convert.MySQLConverter
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.util.FileUtil

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:59
 */
class MySQLOutput( conversion : String => String) extends Output {

  val logger = Logger.logger
  val mysqlConverter = new MySQLConverter

  def write(context: Context, w: Workbook) {

    writeSql(context,w,"insert",mysqlConverter.toInsertSQL _)
    writeSql(context,w,"update",s => {
      mysqlConverter.toUpdateSQL(s,s.ids.map(_.name))
    })
    writeSql(context,w,"delete",s => {
      mysqlConverter.toDeleteSQL(s,s.ids.map(_.name))
    })
    writeSql(context,w,"replace",s => {
      mysqlConverter.toReplaceSQL(s,s.ids.map(_.name))
    })


  }
  def writeInserts(context: Context, w: Workbook) {
  }
  private def writeSql(context: Context, w: Workbook,action : String, convert : Sheet => String) {
    val sqls = w.sheets.filter( !_.ignore).map(convert)
    if (sqls.size > 0){
      val filename = conversion( action + "_" + w.name)
      val path = FileUtil.joinPath(context.workingDir, filename + ".sql")
      logger.log("Save " + action + " sql to %s".format(path))
      FileUtil.saveTo(path,sqls)
    }
  }
}
