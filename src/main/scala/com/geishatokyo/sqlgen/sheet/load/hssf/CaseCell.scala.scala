package com.geishatokyo.sqlgen.sheet.load.hssf

import java.util.{Calendar, Date}
import org.apache.poi.hssf.usermodel.HSSFCell
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Cell
import java.text.SimpleDateFormat
import java.lang.String
import java.util.concurrent.TimeUnit

/**
 *
 * User: takeshita
 * Create: 12/01/24 11:42
 */

object CaseCell {
  def apply(cell: HSSFCell) = {
    if (cell == null) println("type:empty")
    else {
      cell.getCellType match {
        case Cell.CELL_TYPE_STRING => {
          println("type:string " + cell.getStringCellValue)
        }
        case Cell.CELL_TYPE_NUMERIC => {
          if (DateUtil.isCellDateFormatted(cell)) {
            println("type:date " + cell.getDateCellValue)
          } else {
            println("type:numeric " + cell.getNumericCellValue)
          }
        }
        case Cell.CELL_TYPE_BOOLEAN => {
          println("type:boolean " + cell.getBooleanCellValue)
        }
        case Cell.CELL_TYPE_FORMULA => {
          println("type:formula " + cell.getCellFormula)
        }
        case Cell.CELL_TYPE_BLANK => {
          println("type:blank")
        }
      }
    }
  }
}

case class CellValue(raw: HSSFCell, var sqlValue: String) {
  override def toString: String = {
    sqlValue
  }

  def this() = this(null, "")
}


object EmptyCell {

  def unapply(cell: HSSFCell) = {
    if (cell == null) Some(Tuple1(""))
    else {

      cell.getCellType match {
        case Cell.CELL_TYPE_STRING => {
          if (cell.getStringCellValue.length() == 0) {
            Some(Tuple1(""))
          } else {
            None
          }
        }
        case Cell.CELL_TYPE_BLANK => {
          Some(Tuple1(""))
        }
        case _ => None
      }
    }
  }
}

object LongCell {

  def unapply(cell: HSSFCell): Option[Long] = {
    if (cell == null) None
    else {
      try {
        cell.getCellType match {
          case Cell.CELL_TYPE_STRING => {
            Some((cell.getStringCellValue.toLong))
          }
          case Cell.CELL_TYPE_NUMERIC => {
            if (cell.getNumericCellValue % 1.0 == 0) {
              Some((cell.getNumericCellValue.toLong))
            } else {
              None
            }
          }
          case Cell.CELL_TYPE_BOOLEAN => {
            if (cell.getBooleanCellValue) {
              Some((1))
            } else {
              Some((0))
            }
          }
        }
      } catch {
        case e: Exception => None
      }
    }
  }

}

object StringCell {

  def unapply(cell: HSSFCell) = {
    if (cell == null) Some((""))
    else {
      if(cell.getCellType == Cell.CELL_TYPE_STRING){
        Some((cell.getStringCellValue))
      }else if(cell.getCellType == Cell.CELL_TYPE_NUMERIC){
        if (cell.getNumericCellValue % 1.0 == 0){
          Some(cell.getNumericCellValue.toLong.toString)
        }else{
          Some(cell.toString)
        }
      }else{
        Some(cell.toString)
      }
    }
  }
}

object DoubleCell {


  def unapply(cell: HSSFCell): Option[Double] = {
    if (cell == null) None
    else {
      try {
        cell.getCellType match {
          case Cell.CELL_TYPE_STRING => {
            Some((cell.getStringCellValue.toDouble))
          }
          case Cell.CELL_TYPE_NUMERIC => {
            Some((cell.getNumericCellValue.toLong))
          }
          case Cell.CELL_TYPE_BOOLEAN => {
            if (cell.getBooleanCellValue) {
              Some((1.0))
            } else {
              Some((0.0))
            }
          }
        }
      } catch {
        case e: Exception => None
      }
    }
  }
}

object DateCell {

  def unapply(cell: HSSFCell) = {
    if (cell == null) None
    else {
      try {
        cell.getCellType match {
          case Cell.CELL_TYPE_NUMERIC => {
            val v = cell.getDateCellValue
            if(v!= null) Some(v)
            else{
              val n = cell.getNumericCellValue
              val c = Calendar.getInstance()
              c.set(1900,0,1) // エクセルの基準日 1900/01/01
              Some(new Date(c.getTime.getTime + (TimeUnit.DAYS.toMillis(1) * n).toLong))
            }
          }
          case Cell.CELL_TYPE_STRING => {
            Some((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cell.getStringCellValue)))
          }
          case _ => None
        }
      } catch {
        case e: Exception => None
      }
    }
  }
}