package com.geishatokyo.sqlgen.project.input

import java.io.{File, FileInputStream, InputStream}

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.sheet._
import com.geishatokyo.sqlgen.util.FileUtil
import org.apache.poi.ss.usermodel.{DateUtil, WorkbookFactory, FormulaEvaluator}
import org.apache.poi.ss.util.CellUtil
import org.apache.poi.xssf.usermodel.{XSSFCell, XSSFRow, XSSFSheet, XSSFWorkbook}
import org.apache.poi.ss.usermodel

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

    val xls = WorkbookFactory.create(input)

    implicit val formulaEvaluator = xls.getCreationHelper.createFormulaEvaluator()

    val wb = new Workbook()
    val sheetSize = xls.getNumberOfSheets
    wb.addSheets((0 until sheetSize).flatMap(i => loadSheet(xls.getSheetAt(i))).toList)
    wb

  }

  private def loadSheet(xls: usermodel.Sheet)(implicit formulaEvaluator : FormulaEvaluator) : Option[Sheet] = {

    val sheetName = xls.getSheetName

    val rowSize = xls.getPhysicalNumberOfRows
    if(rowSize <= 0) {
      logger.log("Sheet:%s is empty".format(sheetName))
      return None
    }

    val sheet = new Sheet(sheetName)
    loadHeaders(xls,sheet)

    val rows = (1 until rowSize).map(i => {
      val row = xls.getRow(i)
      loadRow(sheet,row)
    }).filter(cells => !cells.forall(_.isEmpty)).toList

    sheet.addRowCells(rows)

    Some(sheet)
  }

  def loadRow(sheet : Sheet, row: usermodel.Row)(implicit formulaEvaluator : FormulaEvaluator) : List[Cell] = {
    if(row == null) return Nil
    (0 until sheet.columnSize).map({
      case index => {
        val cell = row.getCell(index)
        loadCell(sheet,cell)
      }
    }).toList
  }



  def loadCell(sheet : Sheet, _cell : usermodel.Cell)(implicit formulaEvaluator : FormulaEvaluator) : Cell = {
    if(_cell == null) return new Cell(sheet,null)


    new Cell(sheet,getCellValue(_cell))
  }


  def loadHeaders(xls: usermodel.Sheet,sheet : Sheet)(implicit formulaEvaluator : FormulaEvaluator) : Unit = {
    val row = xls.getRow(0)
    val sheetName = sheet.name
    if(row == null) return
    val size = row.getPhysicalNumberOfCells

    val columnNames = (0 until size).map(i => {
      val cell = row.getCell(i)
      val name = getCellValue(cell)
      if(name != null){
        name.toString
      }else "UnkonwnName"
    }).toList
    sheet.addColumns(columnNames :_*)
  }

  def getCellValue(_cell : usermodel.Cell)(implicit formulaEvaluator : FormulaEvaluator) : Any = {

    if(_cell == null) return null

    import org.apache.poi.ss.usermodel.{Cell => PoiCell}

    if(_cell.getCellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA){
      val cell = formulaEvaluator.evaluate(_cell)
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          cell.getStringValue
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          if(DateUtil.isCellDateFormatted(_cell)){
            DateUtil.getJavaDate(cell.getNumberValue)
          }else {
            cell.getNumberValue
          }
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          cell.getBooleanValue
        }
        case PoiCell.CELL_TYPE_BLANK => {
          null
        }
        case _ => {
          null
        }
      }
    }else {
      val cell = _cell
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          cell.getStringCellValue
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          if(DateUtil.isCellDateFormatted(cell)){
            cell.getDateCellValue()
          }else {
            var v = cell.getNumericCellValue
            if(v % 1.0 == 0){
              v.toLong
            }else{
              v
            }
          }
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          cell.getBooleanCellValue
        }
        case PoiCell.CELL_TYPE_BLANK => {
          null
        }
        case _ => {
          null
        }
      }
    }
  }

}
