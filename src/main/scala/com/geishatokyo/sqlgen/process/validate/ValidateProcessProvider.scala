package com.geishatokyo.sqlgen.process.validate

import com.geishatokyo.sqlgen.process.{Proc, ProcessProvider}
import com.geishatokyo.sqlgen.project.ValidateProject
import com.geishatokyo.sqlgen.sheet.{CellUnit, HeaderNotFoundException, Sheet, Workbook}
import com.geishatokyo.sqlgen.project.ValidateProject.{ValidateColumnTask, ValidateRowTask, ValidationTask}
import com.geishatokyo.sqlgen.SQLGenException

/**
 *
 * User: takeshita
 * Create: 12/07/18 12:29
 */

trait ValidateProcessProvider extends ProcessProvider {
  type ProjectType <: ValidateProject

  def validationProc = new ValidationProcess


  class ValidationProcess() extends Proc{
    def name: String = "Validation"

    def apply(workbook: Workbook): Workbook = {
      workbook.foreachSheet(sheet => {
        val v = project.validations(sheet.name)
        validate(sheet,v)
      })
      workbook
    }
    def validate(sheet : Sheet,validations : List[ValidationTask]) = {
      validations.foreach({
        case ValidateColumnTask(columnName,func) => {
          sheet.getColumn(columnName) match{
            case Some(column) => {
              var index = 0
              column.cells.map(c => CellUnit(column.header,c)).foreach(c => {
                if(!func(c)){
                  throw new SQLGenException("Column:%s@%s row:%s is invalid.(Value:'%s')".format(
                    c.header.name,c.header.parent.name,index,c.value
                  ))
                }
                index += 1
              })
            }
            case None => {
              throw new HeaderNotFoundException(sheet.name,columnName)
            }
          }
        }
        case ValidateRowTask(func) => {
          var index = 1
          sheet.foreachRow( row => {
            if(!func(row)){
              throw new SQLGenException("Row at %s@%s is invalid.(Valus:%s)".format(
                index,sheet.name,row
              ))
            }
            index += 1
          })
        }
      })
    }
  }

}
