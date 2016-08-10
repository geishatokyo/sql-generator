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
class XlsOutput(path: String, isXlsx: Boolean) extends Output{

  val logger = Logger.logger


  override def output(context: Context,workbook: Workbook): Unit = {
    // 相対パスチェック
    val path2 = if(path.startsWith(".")){
      path
    }else{
      FileUtil.joinPath("./" ,path)
    }
    //ファイル名チェック
    val name = if(path.takeRight(7).contains('.')){
      path2
    }else{
      FileUtil.joinPath(path2,"%s.%s".format(workbook.name,"xls"))
    }
    val filename = FileUtil.joinPath(context.workingDir,name)

    val xls = new XLSConverter().toHSSFSheet(workbook,isXlsx)
    logger.log("Save " + filename)
    FileUtil.saveTo(filename,xls)
  }
}
