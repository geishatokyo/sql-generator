package com.geishatokyo.sqlgen.process.merge

import com.geishatokyo.sqlgen.process.{OutputProc, Input, Proc}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.I18NUtil

/**
 *
 * User: takeshita
 * Create: 12/07/14 1:58
 */

trait I18NProcessProvider extends WorkbookMergeProcessProvider {
  self : Input =>

  def i18nProc( preProcess  : Proc , afterProcess : Proc) = {
    new I18NProc(preProcess,afterProcess)
  }

  class I18NProc(preProcess : Proc,afterProc : Proc) extends Proc {
    def name: String = "MergeI19NFiles"

    def apply(workbook: Workbook): Workbook = {
      val files = I18NUtil.findI18NFiles(context.workingDir,workbook.name)
      files.foreach( fn => {
        logger.log("Find i18n file:" + fn)
        val cp = workbook.copy()

        val mergeFile = preProcess(load(fn))
        cp.name = mergeFile.name
        val p = new WorkbookMergeProcess(mergeFile)
        afterProc(p(cp))

      })
      workbook
    }
  }

}
