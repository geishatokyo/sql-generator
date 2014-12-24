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

  def ensureSettingProc = new EnsureSettingProcess(this.project)

  def ensureSettingProcFor(otherProject : BaseProject) = new EnsureSettingProcess(otherProject)

  class EnsureSettingProcess(project : BaseProject) extends Proc{
    def name: String = "EnsureSetting"

    def apply(workbook: Workbook): Workbook = {

      project.sheetsEnsureExists.foreach(name => {
        if (workbook.getSheet(name).isEmpty){
          workbook.addSheet(new Sheet(name))
        }
      })

      workbook.foreachSheet(sheet => {
        val defs = project(sheet.name).columnDef.sortBy(_.priority)
        validateSheet(sheet,defs)
      })
      workbook
    }

    def validateSheet( sheet : Sheet, defs : List[ColumnDef]) = {
      import BaseProject._

      val sheetName = sheet.name
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
            c := func(c.asString)
          })
        }
        case SetDefaultValue(cn,dv, when) => {
          logger.log("Set defalt values to column:%s@%s".format(cn,sheetName))
          getOrAddColumn(sheet,cn).cells.foreach(c => {
            val v = c.asString
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
                val v = c.asString
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
                val v = c.asString
                if (when(v)){
                  c :=  convertFunc(refC(index).asString)
                }
                index += 1
              })
            }
            case None => {
              logger.log("Reference column:%s is not found".format(reference))
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
          val setting = project(sheet.name)
          sheet.addColumns(columnName)
          sheet.header(columnName).columnType = setting.columnTypeGuesser(columnName)
          sheet.column(columnName)
        }
      }
    }
  }

}
