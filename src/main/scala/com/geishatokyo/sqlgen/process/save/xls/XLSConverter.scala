package com.geishatokyo.sqlgen.process.save.xls

import com.geishatokyo.sqlgen.core.{DataType, Workbook}
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
 *
 * User: takeshita
 * Create: 12/07/12 0:17
 */

trait XLSConverter {

  def toHSSFSheet( workbook : Workbook, isXlsx: Boolean) : usermodel.Workbook = {
    val hssfWB = if(isXlsx){
      new XSSFWorkbook()
    }else {
      new HSSFWorkbook()
    }

    workbook.sheets.foreach( sheet => {
      val hssfSheet = hssfWB.createSheet(sheet.name.toString)
      val headerRow = hssfSheet.createRow(0)
      sheet.headers.zipWithIndex.foreach({
        case (h,index) => {
          val c = headerRow.createCell(index)
          c.setCellValue(h.name.toString)
        }
      })

      (sheet.rows zipWithIndex).foreach({
        case (row,index) => {
          val hssfRow = hssfSheet.createRow(index + 1)
          row.cells.foreach({
            case cell => {
              val header = cell.header
              val c = hssfRow.createCell(index)
              cell.dataType match{
                case DataType.Integer => c.setCellValue(cell.asLong)
                case DataType.Number => c.setCellValue(cell.asDouble)
                case DataType.Date => {
                  c.setCellValue(cell.asOldDate)
                }
                case DataType.Bool => {
                  c.setCellValue(cell.asBool)
                }
                case DataType.String => c.setCellValue(cell.asString)
                case _ => c.setCellValue(cell.asString)
              }
            }
          })

        }
      })

    })

    hssfWB
  }

}
