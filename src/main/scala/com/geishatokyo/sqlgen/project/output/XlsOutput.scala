package com.geishatokyo.sqlgen.project.output

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.project.flow.{Output}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.XLSConverter
import com.geishatokyo.sqlgen.util.FileUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:54
 */
class XlsOutput(path: String) extends Output{

  val logger = Logger.logger


  override def output(context: Context,workbook: Workbook): Unit = {

    val name = if(path.contains('.')){
      path
    }else{
      FileUtil.joinPath(path,"%s.%s".format(workbook.name,"xls"))
    }
    val filename = FileUtil.joinPath(context.workingDir,name)

    val xls = new XLSConverter().toHSSFSheet(workbook)
    logger.log("Save " + filename)
    FileUtil.saveTo(filename,xls)
  }
}
