package com.geishatokyo.sqlgen.process.merge

import com.geishatokyo.sqlgen.process.{Input, Proc}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.I18NUtil

/**
 *
 * User: takeshita
 * Create: 12/07/14 1:58
 */

trait I18NProcessProvider extends WorkbookMergeProcessProvider {
  self : Input =>

  def mergeI18NWorkbooksProc(preProcess : Proc) : Proc = {
    new I18NProc(preProcess)
  }

  class I18NProc(proc : Proc) extends Proc {
    def name: String = null

    def apply(workbook: Workbook): Workbook = {
      val files = I18NUtil.findI18NFiles(context.workingDir,context.name)
      files.foreach( fn => {
        val p = new WorkbookMergeProcess(fn,proc)
        p.apply(workbook)
      })
      workbook
    }
  }

}
