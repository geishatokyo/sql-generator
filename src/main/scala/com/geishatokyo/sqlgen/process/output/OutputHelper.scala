package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.process.{ProcessProvider, OutputProc}

/**
 *
 * User: takeshita
 * Create: 12/07/15 16:40
 */

trait OutputHelper {
  self : ProcessProvider =>

  /**
   *
   * @param prefix
   * @param ext
   * @param wb
   * @return
   */
  def withWorkbookName(prefix : String,ext : String)(wb : Workbook) = {
    FileUtil.joinPath(context.workingDir,"%s%s.%s".format(prefix,wb.name,ext))
  }
}
