package com.geishatokyo.sqlgen.process.ensure

import com.geishatokyo.sqlgen.process.ProcessProvider
import process.Proc
import project.BaseProject
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import project.BaseProject.{Exists, ColumnDef}
import com.geishatokyo.sqlgen.SQLGenException

/**
 *
 * User: takeshita
 * Create: 12/07/12 22:37
 */

trait EnsureProcessProvider extends ProcessProvider {
  type ProjectType <: BaseProject

  def ensureSettingProc = new EnsureSettingProcess

  class EnsureSettingProcess extends Proc{
    def name: String = "EnsureSetting"

    def apply(workbook: Workbook): Workbook = {
      workbook.foreachSheet(sheet => {
        val defs = project(sheet.name).columnDef.sortBy(_.priority)
        validateSheet(sheet,defs)
      })
      workbook
    }

    def validateSheet( sheet : Sheet, defs : List[ColumnDef]) = {
      import BaseProject._

      val sheetName = sheet.name.value
      defs.foreach({
        case Exists(cn) => {
          if (!sheet.existColumn(cn)){
            logger.log("Add column:%s@%s".format(cn,sheetName))
            sheet.addColumns(cn)
          }
        }
        case ThrowErrorWhenNotExist(cn) => {
          if (!sheet.existColumn(cn)){
            throw new SQLGenException("Column:%s@%s is not defined on sheet.".format(cn,sheetName))
          }
        }
        case Convert(cn, func) => {
          sheet.getColumn(cn) match{
            case Some(c) =>{
              logger.log("Convert values column:%s@%s".format(cn,sheetName))
              c.cells.foreach(c => {
                c := func(c.value)
              })
            }
            case None =>
          }
        }
        case SetDefaultValue(cn,dv, when) => {
          sheet.getColumn(cn) match{
            case Some(c) =>{
              logger.log("Set defalt values to column:%s@%s".format(cn,sheetName))
              c.cells.foreach(c => {
                val v = c.value
                if (when(v)){
                  c := dv
                }
              })
            }
            case None =>
          }
        }
        case ThrowErrorWhen(cn,when) => {
          sheet.getColumn(cn) match{
            case Some(c) =>{
              logger.log("Check value of column:%s@%s".format(cn,sheetName))
              var index = 0
              c.cells.foreach(c => {
                val v = c.value
                if (when(v)){
                  throw new SQLGenException(
                    "Column:%s@%s row:%s value='%s' is not valid.".format(
                      cn,sheetName,index,v))
                }
                index += 1
              })
            }
            case None =>
          }
        }
      })


    }
  }

}
