package com.geishatokyo.sqlgen.sheet.convert

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import com.geishatokyo.sqlgen.sheet.{CellUnit, Workbook}
import com.geishatokyo.sqlgen.sheet.ColumnType

/**
 *
 * User: takeshita
 * Create: 12/07/12 0:17
 */

class XLSConverter {

  def toHSSFSheet( workbook : Workbook) : HSSFWorkbook = {
    val hssfWB = new HSSFWorkbook()

    workbook.foreachSheet( sheet => {
      val hssfSheet = hssfWB.createSheet(sheet.name.toString)
      val headerRow = hssfSheet.createRow(0)
      sheet.headers.zipWithIndex.foreach({
        case (h,index) => {
          val c = headerRow.createCell(index)
          c.setCellValue(h.name.toString)
        }
      })

      sheet.foreachRow({
        case row => {
          val hssfRow = hssfSheet.createRow(row.index + 1)
          row.units.zipWithIndex.foreach({
            case ( CellUnit(header,cell),index) => {
              val c = hssfRow.createCell(index)
              header.columnType match{
                case ColumnType.Integer => c.setCellValue(cell.asDouble)
                case ColumnType.Double => c.setCellValue(cell.asDouble)
                case ColumnType.Date => {
                  if(cell.asDate != null){
                    c.setCellValue(cell.asDate)
                  }else{
                    c.setCellValue("")
                  }
                }
                case ColumnType.String => c.setCellValue(cell.asString)
                case ColumnType.Any => c.setCellValue(cell.asString)
              }
            }
          })

        }
      })

    })

    hssfWB
  }

}
