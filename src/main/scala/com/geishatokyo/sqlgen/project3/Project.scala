package com.geishatokyo.sqlgen.project3

import com.geishatokyo.sqlgen.project3.refs.{ColumnRef}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}

import scala.util.DynamicVariable
import scala.util.matching.Regex
import scalaz.Monad

/**
 * Created by takezoux2 on 15/05/04.
 */
trait Project {

  val currentWorkbook = new DynamicVariable[Workbook](null)

  def sheet(name : String) = {
    val wb = currentWorkbook.value
    wb.get(name)
  }

  def column(name : String)(implicit sheet: Sheet) : ColumnRef = {
    new ColumnRef(sheet,name)
  }

  def rows(implicit sheet: Sheet) = {
    sheet.rows
  }
  def columns(implicit sheet: Sheet) = {
    sheet.columns
  }


  protected var actions : List[(Workbook => Workbook)] = Nil


  def onAllSheet(action: Sheet => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheets.foreach(sheet => action(sheet))
      wb
    }
    this.actions = func :: this.actions
  }

  def onSheet(sheetName: String)(action : Sheet => Any) : Unit = {
    this.actions =  doOnSheet(sheetName,action) _ :: actions
  }
  def onSheet(sheetMatch: Regex)(action: Sheet => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheetsMatchingTo(sheetMatch).foreach(sheet => action(sheet))
      wb
    }
    this.actions = func :: this.actions
  }

  def ignore()(implicit sheet: Sheet) = {
    sheet.ignore
  }

  private def doOnSheet(sheetName: String,action : Sheet => Any)(wb: Workbook) = {
    wb.getSheet(sheetName).foreach(sheet => action(sheet))
    wb
  }

  def apply(workbook : Workbook) = {
    currentWorkbook.withValue(workbook){
      actions.reverse.foldLeft(workbook)((wb,ac) => ac(wb))
    }
  }

  def addAction(action: Workbook => Workbook) = {
    actions = action :: actions
  }


  def ++(next: Project) = {
    val p = new EmptyProject()
    p.actions = this.actions ++ next.actions
    p
  }





}

class EmptyProject extends Project{

}