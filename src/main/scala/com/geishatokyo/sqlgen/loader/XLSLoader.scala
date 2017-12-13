package com.geishatokyo.sqlgen.loader

import java.io.InputStream

import com.geishatokyo.sqlgen.core.{NullVar, Sheet, Variable, Workbook}
import com.geishatokyo.sqlgen.logger.Logger
import org.apache.poi.ss.usermodel
import org.apache.poi.ss.usermodel.{CellType, DateUtil, FormulaEvaluator, WorkbookFactory}

/**
  * Created by takezoux2 on 2017/06/14.
  */
class XLSLoader extends Loader {
  val logger = Logger.logger

  override def load(name: String, input: InputStream): Workbook = {
    //Logger.log(s"Load xls ${name}")
    val xls = WorkbookFactory.create(input)
    implicit val formulaEvaluator = xls.getCreationHelper.createFormulaEvaluator()

    val wb = new Workbook(name)

    val size = xls.getNumberOfSheets()
    (0 until size).foreach(i => {
      loadSheet(wb, xls.getSheetAt(i)) match{
        case Some(s) => wb.addSheet(s)
        case None =>
      }
    })


    wb
  }


  private def loadSheet(wb: Workbook, xls: usermodel.Sheet)(implicit formulaEvaluator : FormulaEvaluator) : Option[Sheet] = {

    val sheetName = xls.getSheetName

    val rowSize = xls.getPhysicalNumberOfRows
    if(rowSize <= 0) {
      logger.log("Sheet:%s is empty".format(sheetName))
      return None
    }

    val sheet = new Sheet(wb, sheetName)
    loadHeaders(xls,sheet)

    val rows = (1 until rowSize).map(i => {
      val row = xls.getRow(i)
      loadRow(sheet,row)
    }).filter(vars => !vars.forall(_.isEmpty))


    rows.foreach(row => {
      sheet.addRow(row:_*)
    })

    Some(sheet)
  }

  def loadRow(sheet : Sheet, row: usermodel.Row)(implicit formulaEvaluator : FormulaEvaluator) : List[Variable] = {
    if(row == null) return Nil

    // 空白行はスキップ
    if(row.getPhysicalNumberOfCells == 0) return Nil
    // #開始の行もスキップ
    if(
      row.getCell(0) != null &&
      row.getCell(0).getCellTypeEnum == CellType.STRING &&
      row.getCell(0).getStringCellValue.startsWith("#")
    ) return Nil

    (0 until sheet.columnSize).map({
      case index => {
        val cell = row.getCell(index)
        loadCell(sheet,cell)
      }
    }).toList
  }



  def loadCell(sheet : Sheet, _cell : usermodel.Cell)(implicit formulaEvaluator : FormulaEvaluator) : Variable = {
    if(_cell == null) return NullVar
    else Variable(getCellValue(_cell))
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
    sheet.addHeaders(columnNames :_*)
  }

  def getCellValue(_cell : usermodel.Cell)(implicit formulaEvaluator : FormulaEvaluator) : Any = {
    if(_cell == null) return null
    import org.apache.poi.ss.usermodel.{Cell => PoiCell}

    if(_cell.getCellTypeEnum == CellType.FORMULA){
      val cell = formulaEvaluator.evaluate(_cell)
      cell.getCellTypeEnum match{
        case CellType.STRING => {
          cell.getStringValue
        }
        case CellType.NUMERIC => {
          if(DateUtil.isCellDateFormatted(_cell)){
            DateUtil.getJavaDate(cell.getNumberValue)
          }else {
            cell.getNumberValue
          }
        }
        case CellType.BOOLEAN => {
          cell.getBooleanValue
        }
        case CellType.BLANK => {
          null
        }
        case _ => {
          null
        }
      }
    }else {
      val cell = _cell
      cell.getCellTypeEnum match{
        case CellType.STRING => {
          cell.getStringCellValue
        }
        case CellType.NUMERIC => {
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
        case CellType.BOOLEAN => {
          cell.getBooleanCellValue
        }
        case CellType.BLANK => {
          null
        }
        case _ => {
          null
        }
      }
    }
  }
}
