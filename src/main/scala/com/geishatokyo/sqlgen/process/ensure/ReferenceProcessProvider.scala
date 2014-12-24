package com.geishatokyo.sqlgen.process.ensure

import com.geishatokyo.sqlgen.process.{Proc, ProcessProvider}
import com.geishatokyo.sqlgen.project.{ReferenceProject, BaseProject}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
 * Sheetをまたいだ値の参照を行う。
 * User: takeshita
 * Create: 12/09/03 17:20
 */
trait ReferenceProcessProvider extends ProcessProvider {

  type ProjectType <: ReferenceProject

  def referenceProc = {
    new ReferenceProcess(this.project)
  }

  class ReferenceProcess( project : ReferenceProject) extends Proc {
    def name: String = "Reference"

    def apply(workbook: Workbook): Workbook = {
      workbook.foreachSheet(sheet => {
        val settings = project.getReferenceSettings(sheet.name)
        settings.foreach(ref => {
          if (!sheet.existColumn(ref.columnName)){
            sheet.addColumns(ref.columnName)
          }
          sheet.foreachRow(row => {
            val v = row(ref.columnName)
            if (ref.whenFunc(v.asString)){
              val sheet = workbook(ref.sheetName)
              sheet.findFirstRowWhere( refRow => {ref.whereFunc(row,refRow)}) match{
                case Some(refRow) => {
                  v := ref.valueFunc(refRow)
                }
                case None => {
                  // nothing to do
                  logger.log("Not found reference row")
                }
              }
            }
          })
        })
      })
      workbook
    }
  }

}
