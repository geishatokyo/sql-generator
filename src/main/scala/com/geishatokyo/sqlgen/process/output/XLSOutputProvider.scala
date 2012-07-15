package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.process.{Proc, ProcessProvider}
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.XLSConverter
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/07/13 19:02
 */

trait XLSOutputProvider extends ProcessProvider with OutputHelper {

  def outputXlsProc( prefix : String = "converted_") = {
    new XLSOutputProcess(withWorkbookName(prefix,"xls"))
  }

  class XLSOutputProcess(getPath : Workbook => String) extends Proc{

    def name = "SaveXLS"

    def apply(workbook: Workbook): Workbook = {
      val xls = new XLSConverter().toHSSFSheet(workbook)
      val filename = getPath(workbook)
      logger.log("Save " + filename)
      FileUtil.saveTo(filename,xls)
      workbook
    }
  }
}
