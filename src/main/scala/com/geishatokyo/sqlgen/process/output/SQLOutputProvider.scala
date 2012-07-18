package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.process.{OutputProc, ProcessProvider, SeqProc, Proc}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.MySQLConverter
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.project.BaseProject

/**
 *
 * User: takeshita
 * Create: 12/07/12 19:01
 */

trait SQLOutputProvider extends ProcessProvider with OutputHelper{
  val mysqlConverter = new MySQLConverter

  def outputSqlProc(prefix : String = "") = new SeqProc(
    List(
      new InsertSQL(withWorkbookName("insert_" + prefix,"sql")),
      new DeleteSQL(withWorkbookName("delete_" + prefix,"sql")),
      new UpdateSQL(withWorkbookName("update_" + prefix,"sql"))
    )
  )

  def outputInsertSqlProc(prefix : String = "") = {
    new InsertSQL(withWorkbookName("insert_" + prefix,"sql"))
  }
  def outputDeleteSqlProc(prefix : String = "") = {
    new DeleteSQL(withWorkbookName("delete_" + prefix,"sql"))
  }
  def outputUpdateSqlProc(prefix : String = "") = {
    new UpdateSQL(withWorkbookName("update_" + prefix,"sql"))
  }

  class InsertSQL( getPath : Workbook => String) extends OutputProc{
    def name: String = "GenerateInsertSQL"

    def apply(workbook: Workbook): Workbook = {
      val sql = workbook.foreachSheet(sheet => {
        mysqlConverter.toInsertSQL(sheet)
      })
      val path = getPath(workbook)
      logger.log("Save insert sql to %s".format(path))
      FileUtil.saveTo(path,sql)
      workbook
    }
  }

  class DeleteSQL(getPath : Workbook => String) extends OutputProc{
    def name: String = "GenerateDeleteSQL"
    def apply(workbook: Workbook): Workbook = {
      val sql = workbook.foreachSheet(sheet => {
        mysqlConverter.toDeleteSQL(sheet,sheet.ids.map(_.name.toString))
      })
      val path = getPath(workbook)
      logger.log("Save delete sql to %s".format(path))
      FileUtil.saveTo(path,sql)
      workbook
    }
  }

  class UpdateSQL(getPath : Workbook => String) extends OutputProc{
    def name: String = "GenerateUpdateSQL"
    def apply(workbook: Workbook): Workbook = {
      val sql = workbook.foreachSheet(sheet => {
        mysqlConverter.toUpdateSQL(sheet,sheet.ids.map(_.name.toString))
      })
      val path = getPath(workbook)
      logger.log("Save delete sql to %s".format(path))
      FileUtil.saveTo(path,sql)
      workbook
    }

  }

}
