package com.geishatokyo.sqlgen.process.ensure

import com.geishatokyo.sqlgen.process.ProcessProvider
import com.geishatokyo.sqlgen.process.Proc
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.sheet.{Column, Sheet, Workbook}
import com.geishatokyo.sqlgen.project.BaseProject.{Exists, ColumnDef}
import com.geishatokyo.sqlgen.SQLGenException

/**
 * To ensure input file data is correct.
 * This process should be invoked before process other conversion process.
 * Ensure functions don't throw exception except ThrowError.
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
          getOrAddColumn(sheet,cn)
        }
        case ThrowErrorWhenNotExist(cn) => {
          if (!sheet.existColumn(cn)){
            throw new SQLGenException("Column:%s@%s is not defined on sheet.".format(cn,sheetName))
          }
        }
        case Convert(cn, func) => {
          logger.log("Convert values column:%s@%s".format(cn,sheetName))
          getOrAddColumn(sheet,cn).cells.foreach(c => {
            c := func(c.value)
          })
        }
        case SetDefaultValue(cn,dv, when) => {
          logger.log("Set defalt values to column:%s@%s".format(cn,sheetName))
          getOrAddColumn(sheet,cn).cells.foreach(c => {
            val v = c.value
            if (when(v)){
              c := dv
            }
          })
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
        case ReferColumn(cn, reference,convertFunc , when ) => {
          sheet.getColumn(reference) match{
            case Some(refC) => {
              logger.log("Check value of column:%s@%s".format(cn,sheetName))
              var index = 0
              getOrAddColumn(sheet,cn).cells.foreach(c => {
                val v = c.value
                if (when(v)){
                  c :=  convertFunc(refC(index))
                }
                index += 1
              })
            }
            case None => {
            }
          }
        }
      })

    }

    protected def getOrAddColumn(sheet : Sheet,columnName : String) : Column = {
      sheet.getColumn(columnName) match{
        case Some(c) =>{
          c
        }
        case None => {
          logger.log("Add column:%s@%s".format(columnName,sheet.name))
          sheet.addColumns(columnName)
          sheet.column(columnName)
        }
      }
    }
  }

}
