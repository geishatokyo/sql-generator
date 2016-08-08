package com.geishatokyo.sqlgen.project3.input

import java.io.{File, FileInputStream, InputStream}

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.sheet._
import com.geishatokyo.sqlgen.util.FileUtil
import org.apache.poi.hssf.usermodel.{HSSFCell, HSSFRow, HSSFSheet, HSSFWorkbook}
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
    loadHeaders(xls,sheet)



    val rows = (1 until rowSize).map(i => {
      val row = xls.getRow(i)
      loadRow(sheet,row)
    }).filter(cells => !cells.forall(_.isEmpty)).toList

    sheet.addRowCells(rows)

    Some(sheet)
  }

  def loadRow(sheet : Sheet, row: HSSFRow)(implicit formulaEvaluator : FormulaEvaluator) : List[Cell] = {
    if(row == null) return Nil
    (0 until sheet.columnSize).map({
      case index => {
        val cell = row.getCell(index)
        loadCell(sheet,cell)
      }
    }).toList
  }



  def loadCell(sheet : Sheet, _cell : HSSFCell)(implicit formulaEvaluator : FormulaEvaluator) : Cell = {
    if(_cell == null) return new Cell(sheet,null)

    import org.apache.poi.ss.usermodel.{Cell => PoiCell}

    if(_cell.getCellType == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA){
      val cell = formulaEvaluator.evaluate(_cell)
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          new Cell(sheet,cell.getStringValue)
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          new Cell(sheet, cell.getNumberValue)
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          new Cell(sheet, cell.getBooleanValue)
        }
        case PoiCell.CELL_TYPE_BLANK => {
          new Cell(sheet,null)
        }
        case _ => {
          new Cell(sheet,null)
        }
      }
    }else {
      val cell = _cell
      cell.getCellType match{
        case PoiCell.CELL_TYPE_STRING => {
          new Cell(sheet,cell.getStringCellValue)
        }
        case PoiCell.CELL_TYPE_NUMERIC => {
          new Cell(sheet, cell.getNumericCellValue)
        }
        case PoiCell.CELL_TYPE_BOOLEAN => {
          new Cell(sheet, cell.getBooleanCellValue)
        }
        case PoiCell.CELL_TYPE_BLANK => {
          new Cell(sheet,null)
        }
        case _ => {
          new Cell(sheet,null)
        }
      }
    }
  }


  def loadHeaders(xls: HSSFSheet,sheet : Sheet) : Unit = {
    val row = xls.getRow(0)
    val sheetName = sheet.name
    if(row == null) return
    val size = row.getPhysicalNumberOfCells

    val columnNames = (0 until size).map(i => {
      val cell = row.getCell(i)
      val name = cell.getStringCellValue
      name
    }).toList
    sheet.addColumns(columnNames :_*)
  }

}
