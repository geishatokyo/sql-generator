package com.geishatokyo.sqlgen.project2.input

import java.io.{File, FileInputStream, InputStream}
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import com.geishatokyo.sqlgen.sheet.{ColumnType, ColumnHeader, Sheet, Workbook}
import com.geishatokyo.sqlgen.sheet.load.hssf._
import scala.Some
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.util.FileUtil
import org.apache.poi.ss.usermodel.FormulaEvaluator

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:27
 */
object XLSLoader {
  val logger = Logger.logger

  def load(filename : String) : Workbook = {
    load(new File(filename))
  }
  def load(file : File) : Workbook = {
    val input = new FileInputStream(file)
    try{
      val wb = load(input)
      val (dir,name,ext) = FileUtil.splitPathAndNameAndExt(file.getAbsolutePath)
      wb.name = name

      wb
    }finally{
      input.close()
    }
  }


  def load(input : InputStream) : Workbook = {

    val xls = new HSSFWorkbook(input)

    implicit val formulaEvaluator = xls.getCreationHelper.createFormulaEvaluator()

    val wb = new Workbook()
    val sheetSize = xls.getNumberOfSheets
    wb.addSheets((0 until sheetSize).flatMap(i => loadSheet(xls.getSheetAt(i))).toList)
    wb

  }

  private def loadSheet(xls: HSSFSheet)(implicit formulaEvaluator : FormulaEvaluator) : Option[Sheet] = {

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

  private def loadRow(headers: List[(Int, ColumnHeader)], row: HSSFRow)(implicit formulaEvaluator : FormulaEvaluator) : List[String] = {
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

  private def loadCellValue( cell : HSSFCell , columnType : ColumnType.Value)(implicit formulaEvaluator : FormulaEvaluator) : String = {
    if(cell == null) return null

    if(cell.getCellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA){
      val value = formulaEvaluator.evaluate(cell)

      if(columnType == ColumnType.Date) {

        (value.getNumberValue * 1000).toLong.toString
      }else{
        value.formatAsString()
      }

    }else{
      columnType match{
        case ColumnType.Integer => {
          cell match{
            case EmptyCell(_) => "0"
            case LongCell(v) => v.toString
            case _ => null
          }
        }
        case ColumnType.Double => {
          cell match{
            case EmptyCell(_) => "0"
            case DoubleCell(v) => v.toString
            case _ => null
          }

        }
        case ColumnType.String => {
          cell match{
            case EmptyCell(_) => ""
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
            case EmptyCell(_) => ""
            case StringCell(v) => v
          }
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
