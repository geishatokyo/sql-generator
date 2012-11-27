package com.geishatokyo.sqlgen.util

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.sheet.convert.{MySQLConverter, XLSConverter}

/**
 * 
 * User: takeshita
 * DateTime: 12/11/27 17:04
 */
object SaveUtil {

  var xlsConverter = new XLSConverter
  val sqlConverter = new MySQLConverter

  def saveAsXls(filename : String,wb : Workbook) = {
    val xls = xlsConverter.toHSSFSheet(wb)
    FileUtil.saveTo(filename,xls)
  }

  def saveAsInsertSQL(filename : String ,wb : Workbook) = {
    val sql = wb.sheets.flatMap(sheet => {
      if (!sheet.ignore){
        Some(sqlConverter.toInsertSQL(sheet))
      }else{
        None
      }
    })
    FileUtil.saveTo(filename,sql)
  }
  def saveAsUpdateSQL(filename : String ,wb : Workbook) = {
    val sql = wb.sheets.flatMap(sheet => {
      if (!sheet.ignore){
        Some(sqlConverter.toUpdateSQL(sheet,sheet.ids.map(_.name.value)))
      }else{
        None
      }
    })
    FileUtil.saveTo(filename,sql)
  }
  def saveAsDeleteSQL(filename : String ,wb : Workbook) = {
    val sql = wb.sheets.flatMap(sheet => {
      if (!sheet.ignore){
        Some(sqlConverter.toDeleteSQL(sheet,sheet.ids.map(_.name.value)))
      }else{
        None
      }
    })
    FileUtil.saveTo(filename,sql)
  }
}
