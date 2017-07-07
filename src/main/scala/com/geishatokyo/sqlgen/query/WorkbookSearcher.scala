package com.geishatokyo.sqlgen.query

import com.geishatokyo.sqlgen.core.{Row, Sheet, Workbook}
import com.geishatokyo.sqlgen.logger.Logger

/**
  * Created by takezoux2 on 2017/07/07.
  */
class WorkbookSearcher {


  def findRows(wb: Workbook, q: Query) : List[Row] = {
    wb.getSheet(q.from.sheetName) match{
      case Some(sheet) => findRowsInSheet(sheet,q.where)
      case None => Nil
    }
  }

  def findFirstRow(wb : Workbook, q: Query) : Option[Row] = {
    wb.getSheet(q.from.sheetName) match{
      case Some(sheet) => {
        sheet.rows.find(isRowMatch(_, q.where))
      }
      case None => None
    }
  }

  protected def findRowsInSheet(sheet: Sheet, c: Condition): List[Row] = {

    sheet.rows.filter(row => {
      isRowMatch(row,c)
    }).toList

  }

  protected def isRowMatch(row: Row, c: Condition) : Boolean = {
    c match{
      case Eq(column, value) => {
        row.getHeader(column).isDefined &&
        row(column) == value
      }
      case Range(column, min, max) => {
        val d = row(column).asDouble
        row.getHeader(column).isDefined &&
        min <= d && d <= max
      }
      case RegexMatch(column,r ) => {
        val s = row(column).asString
        row.getHeader(column).isDefined &&
        r.findFirstIn(s).isDefined
      }
      case And(c1, c2) => {
        isRowMatch(row,c1) && isRowMatch(row, c2)
      }
      case Or(c1, c2) => {
        isRowMatch(row,c1) || isRowMatch(row, c2)
      }
    }
  }

}
