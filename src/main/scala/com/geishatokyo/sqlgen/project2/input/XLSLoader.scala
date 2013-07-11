package com.geishatokyo.sqlgen.project2.input

import java.io.{File, FileInputStream, InputStream}
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import com.geishatokyo.sqlgen.sheet.{ColumnType, ColumnHeader, Sheet, Workbook}
import com.geishatokyo.sqlgen.sheet.load.hssf._
import scala.Some
import com.geishatokyo.sqlgen.logger.Logger

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:27
 */
object XLSLoader {
  val logger = Logger.logger

  def load(filename : String) : Workbook = {
    val input = new FileInputStream(filename)
    try{
      load(input)
    }finally{
      input.close()
    }
  }
  def load(file : File) : Workbook = {
    val input = new FileInputStream(file)
    try{
      load(input)
    }finally{
      input.close()
    }
  }


  def load(input : InputStream) : Workbook = {

    val xls = new HSSFWorkbook(input)
    val wb = new Workbook()
    val sheetSize = xls.getNumberOfSheets
    wb.addSheets((0 until sheetSize).flatMap(i => loadSheet(xls.getSheetAt(i))).toList)
    wb

  }

  private def loadSheet(xls: HSSFSheet) : Option[Sheet] = {

    val sheetName = xls.getSheetName

    val rowSize = xls.getPhysicalNumberOfRows
    if(rowSize <= 0) {
      logger.log("Sheet:%s is empty".format(sheetName))
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

  private def loadRow(headers: List[(Int, ColumnHeader)], row: HSSFRow) : List[String] = {
    if(row == null) return Nil
    headers.map({
      case (index,h) => {
        val cell = row.getCell(index)
        val v = loadCellValue(cell,h.columnType)
        if(v != null){
          v.trim()
        }else{
          v
        }
      }
    })
  }

  private def loadCellValue( cell : HSSFCell , columnType : ColumnType.Value) : String = {
    if(cell == null) return null
    columnType match{
      case ColumnType.Integer => {
        cell match{
          case EmptyCell(_) => null
          case LongCell(v) => v.toString
          case _ => null
        }
      }
      case ColumnType.Double => {
        cell match{
          case EmptyCell(_) => null
          case DoubleCell(v) => v.toString
          case _ => null
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


  private def loadHeaders(xls: HSSFSheet,sheet : Sheet): List[(Int, ColumnHeader)] = {
    val row = xls.getRow(0)
    val sheetName = sheet.name.value
    if(row == null) return Nil
    val size = row.getPhysicalNumberOfCells


    val headersOnFile = (0 until size).map(i => {
      val cell = row.getCell(i)
      cell match {
        case StringCell(v) => {
          i -> v.trim()
        }
        //case _ => throw new Exception("There is wrong header column.row:0 column:%s".format(i))
      }
    }).filter(p => p._2.length > 0).toList

    sheet.addColumns(headersOnFile.map(_._2): _*)

    //find and set ids
    headersOnFile.map(_._1).zip(sheet.headers)
  }
}
