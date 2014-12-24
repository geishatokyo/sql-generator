package com.geishatokyo.sqlgen.sheet.load.hssf

import java.io.{FileInputStream, InputStream}
import com.geishatokyo.sqlgen.sheet._
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
import com.geishatokyo.sqlgen.sheet.load.SheetLoader
import java.text.SimpleDateFormat
import com.geishatokyo.sqlgen.logger.Logger
import scala.Some
import org.apache.poi.ss.usermodel.FormulaEvaluator

/**
 *
 * User: takeshita
 * Create: 12/07/12 11:42
 */

class XLSSheetLoader(nameMapper : NameMapper = null) extends SheetLoader {

  val logger = Logger

  def load(input: InputStream): Workbook = {
    val xls = new HSSFWorkbook(input)
    val wb = new Workbook()
    val sheetSize = xls.getNumberOfSheets
    implicit val formulaEvaluator = xls.getCreationHelper.createFormulaEvaluator()
    wb.addSheets((0 until sheetSize).flatMap(i => loadSheet(xls.getSheetAt(i))).toList)
    wb
  }


  def loadSheet(xls: HSSFSheet)(implicit formulaEvaluator : FormulaEvaluator) : Option[Sheet] = {

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
      loadRow(sheet,headers,row)
    }).filter(cells => !cells.forall(_.isEmpty))

    sheet.addRowCells(rows.toList)


    Some(sheet)
  }

  def loadRow(sheet : Sheet,headers: List[ColumnHeader], row: HSSFRow)(implicit formulaEvaluator : FormulaEvaluator) : List[Cell] = {
    if(row == null) return Nil
    (headers zipWithIndex ).map({
      case (h,index) => {
        val cell = row.getCell(index)
        loadCell(sheet,cell)
      }
    })
  }

  def loadCell(sheet : Sheet, _cell : HSSFCell)(implicit formulaEvaluator : FormulaEvaluator) : Cell = {
    if(_cell == null) return null

    import org.apache.poi.ss.usermodel.{Cell => PoiCell}

    if(_cell.getCellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA){
      val cell = formulaEvaluator.evaluate(_cell)
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          Cell(sheet,cell.getStringValue)
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          Cell(sheet, cell.getNumberValue)
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          Cell(sheet, cell.getBooleanValue)
        }
        case PoiCell.CELL_TYPE_BLANK => {
          Cell(sheet,null)
        }
        case _ => {
          Cell(sheet,null)
        }
      }
    }else {
      val cell = _cell
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          Cell(sheet,cell.getStringCellValue)
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          Cell(sheet, cell.getNumericCellValue)
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          Cell(sheet, cell.getBooleanCellValue)
        }
        case PoiCell.CELL_TYPE_BLANK => {
          Cell(sheet,null)
        }
        case _ => {
          Cell(sheet,null)
        }
      }
    }
  }


  def loadHeaders(xls: HSSFSheet,sheet : Sheet): List[ColumnHeader] = {
    val row = xls.getRow(0)
    val sheetName = sheet.name
    if(row == null) return Nil
    val size = row.getPhysicalNumberOfCells

    (0 until size).map(i => {
      val cell = row.getCell(i)
      val name = cell.getStringCellValue
      new ColumnHeader(sheet,name)
    }).toList
  }

}
