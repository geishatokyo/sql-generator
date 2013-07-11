package com.geishatokyo.sqlgen.project2.output

import com.geishatokyo.sqlgen.project2.Output
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.XLSConverter
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.logger.Logger

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:54
 */
class XlsOutput extends Output{

  val logger = Logger.logger
  def write(context: Context, w: Workbook) {

    val xls = new XLSConverter().toHSSFSheet(w)

    val filename = FileUtil.joinPath(context.workingDir,"%s.%s".format(w.name,"xls"))
    logger.log("Save " + filename)
    FileUtil.saveTo(filename,xls)

  }
}
