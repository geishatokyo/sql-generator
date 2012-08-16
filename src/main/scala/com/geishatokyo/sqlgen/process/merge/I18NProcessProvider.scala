package com.geishatokyo.sqlgen.process.merge

import com.geishatokyo.sqlgen.process.{OutputProc,  Proc}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.I18NUtil
import com.geishatokyo.sqlgen.process.input.SingleXLSLoader

/**
 *
 * User: takeshita
 * Create: 12/07/14 1:58
 */

trait I18NProcessProvider extends WorkbookMergeProcessProvider {


  def findAndLoadI18NWorkbooks(name : String) = {
    val files = I18NUtil.findI18NFiles(context.workingDir,name)
    files.map( fn => {
      logger.log("Find i18n file:" + fn)
      load(fn)
    })
  }

  def i18nProc( preProcess  : Proc , afterProcess : Proc) = {
    new I18NProc(preProcess,afterProcess)
  }

  class I18NProc(preProcess : Proc,afterProc : Proc) extends Proc {
    def name: String = "MergeI18NFiles"

    def apply(workbook: Workbook): Workbook = {
      findAndLoadI18NWorkbooks(workbook.name).foreach( wb => {
        val cp = workbook.copy()
        val mergeFile = preProcess(wb)
        cp.name = mergeFile.name
        val p = new WorkbookMergeProcess(mergeFile)
        afterProc(p(cp))

      })
      workbook
    }
  }

}
