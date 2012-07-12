package com.geishatokyo.sqlgen.sheet.load.hssf

import java.io.{FileInputStream, InputStream}
import com.geishatokyo.sqlgen.sheet.{ColumnType, ColumnHeader, Sheet, Workbook}
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import com.geishatokyo.sqlgen.sheet.load.SheetLoader
import java.text.SimpleDateFormat
import com.geishatokyo.sqlgen.logger.Logger

/**
 *
 * User: takeshita
 * Create: 12/07/12 11:42
 */

class XLSSheetLoader(nameMapper : NameMapper,
                     typeGuesser : ColumnTypeGuesser) extends SheetLoader {

  val logger = Logger

  def load(input: InputStream): Workbook = {
    val xls = new HSSFWorkbook(input)
    val wb = new Workbook()
    val sheetSize = xls.getNumberOfSheets
    wb.addSheets((0 until sheetSize).flatMap(i => loadSheet(xls.getSheetAt(i))).toList)
    wb
  }


  def loadSheet(xls: HSSFSheet) : Option[Sheet] = {

    val sheetName = nameMapper.mapSheetName(xls.getSheetName)
    if(sheetName != xls.getSheetName){
      logger.log("Convert sheet name from %s to %s.".format(xls.getSheetName,sheetName))
    }
    val rowSize = xls.getPhysicalNumberOfRows
    if(rowSize <= 0) {
      logger.log("Sheet:%s is empty".format(sheetName))
      return None
    }
    if (nameMapper.isIgnoreSheet_?(sheetName)){
      logger.log("Sheet:%s is ignored".format(sheetName))
      return None
    }

    val sheet = new Sheet(sheetName)
    val headers = loadHeaders(xls,sheet)


    val rows = (1 until rowSize).map(i => {
      val row = xls.getRow(i)
      loadRow(headers,row)
    }).filter(values => !values.forall(v => v == null || v.length == 0))

    sheet.addRows(rows.toList)

    Some(sheet)
  }

  def loadRow(headers: List[(Int, ColumnHeader)], row: HSSFRow) : List[String] = {
    headers.map({
      case (index,h) => {
        val cell = row.getCell(index)
        val v = loadCellValue(cell,h.columnType)
        v
      }
    })
  }

  def loadCellValue( cell : HSSFCell , columnType : ColumnType.Value) = {
    columnType match{
      case ColumnType.Integer => {
        cell match{
          case EmptyCell(_) => null
          case LongCell(v) => v.toString
        }
      }
      case ColumnType.Double => {
        cell match{
          case EmptyCell(_) => null
          case DoubleCell(v) => v.toString
        }

      }
      case ColumnType.String => {
        cell match{
          case EmptyCell(_) => null
          case StringCell(v) => v
        }
      }
      case ColumnType.Date => {
        cell match{
          case EmptyCell(_) => null
          case DateCell(v) => v.getTime.toString
        }

      }
      case ColumnType.Any => {
        cell match{
          case EmptyCell(_) => null
          case StringCell(v) => v
        }
      }
    }
  }


  def loadHeaders(xls: HSSFSheet,sheet : Sheet): List[(Int, ColumnHeader)] = {
    val row = xls.getRow(0)
    val sheetName = sheet.name.value
    if(row == null) return Nil
    val size = row.getPhysicalNumberOfCells

    val nameMap = nameMapper.columnNameMapperFor(sheetName)
    val typeMap = typeGuesser.guesserFor(sheetName)
    val ignoreCols = typeGuesser.isIgnoreColumn_?(sheetName)

    val headersOnFile = (0 until size).map(i => {
      val cell = row.getCell(i)
      cell match {
        case StringCell(v) => {
          i -> v.trim()
        }
        case _ => throw new Exception("There is wrong header column.row:0 column:%s".format(i))
      }
    }).filter(p => p._2.length > 0).map(p => p._1 -> nameMap(p._2)).toList

    sheet.addColumns(headersOnFile.map(_._2): _*)
    sheet.headers.foreach(ch => {
      ch.columnType = typeMap(ch.name)
      ch.output_? = !ignoreCols(ch.name)
      logger.log("Column:%s@%s is %s".format(ch.name,sheetName,ch.columnType))
    })
    headersOnFile.map(_._1).zip(sheet.headers)
  }

}
