package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.process.ProcessProvider
import com.geishatokyo.sqlgen.process.{SeqProc, Proc}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.MySQLConverter
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/07/12 19:01
 */

trait SQLOutputProvider extends ProcessProvider{

  val mysqlConverter = new MySQLConverter

  val outputSqlProc = new SeqProc(
    List(new InsertSQL,
    new DeleteSQL)
  )

  class InsertSQL extends Proc{
    def name: String = "GenerateInsertSQL"

    def apply(workbook: Workbook): Workbook = {
      val sql = workbook.foreachSheet(sheet => {
        mysqlConverter.toInsertSQL(sheet)
      })
      val path = FileUtil.joinPath(context.workingDir,"insert_%s.sql".format(context.name))
      logger.log("Save insert sql to %s".format(path))
      FileUtil.saveTo(path,sql)
      workbook
    }
  }

  class DeleteSQL extends Proc{
    def name: String = "GenerateDeleteSQL"
    def apply(workbook: Workbook): Workbook = {
      val sql = workbook.foreachSheet(sheet => {
        mysqlConverter.toDeleteSQL(sheet,List("id"))
      })
      val path = FileUtil.joinPath(context.workingDir,"delete_%s.sql".format(context.name))
      logger.log("Save delete sql to %s".format(path))
      FileUtil.saveTo(path,sql)
      workbook
    }
  }

}
