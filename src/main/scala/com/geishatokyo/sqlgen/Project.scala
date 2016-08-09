package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.project.refs.{ColumnRef, SheetScope}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}

import scala.util.DynamicVariable
import scala.util.matching.Regex

/**
 * Created by takezoux2 on 15/05/04.
 */
trait Project {

  protected val currentWorkbook = new DynamicVariable[Workbook](null)

  protected val currentSheet = new DynamicVariable[Sheet](null)

  val sheetScope = new SheetScope

  def sheet = {
    val s = currentSheet.value
    if(s == null) throw new Exception("Not sheet scope")
    s
  }

  def sheet(name : String) = {
    val wb = currentWorkbook.value
    if(wb == null) throw new Exception("Not workbook scope")
    wb.get(name)
  }


  def column(name : String) : ColumnRef = {
    new ColumnRef(sheet,name,sheetScope)
  }

  def rows = {
    sheet.rows
  }
  def columns = {
    sheet.columns
  }

  def findById(id: Any) = {
    val idColumn = sheet.ids.headOption.getOrElse(throw new Exception(s"Sheet:${sheet.name} has no ids"))
    rows.find(r => {
      r(idColumn.name) ~== id
    })
  }


  protected var actions : List[(Workbook => Workbook)] = Nil
  protected var postActions : List[Workbook => Workbook] = Nil


  def onAllSheet(action: Sheet => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheets.foreach(sheet => action(sheet))
      wb
    }
    this.actions = func :: this.actions
  }

  def onSheet(sheetName: String)(action : Sheet => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.getSheet(sheetName).foreach(sheet => {
        currentSheet.withValue(sheet){action(sheet)}
      })
      wb
    }
    this.actions =  func :: actions
  }
  def onSheet(sheetMatch: Regex)(action: Sheet => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheetsMatchingTo(sheetMatch).foreach(sheet => {
        currentSheet.withValue(sheet){action(sheet)}
      })
      wb
    }
    this.actions = func :: this.actions
  }

  def ignore()(implicit sheet: Sheet) = {
    sheet.ignore
  }


  def apply(workbook : Workbook) = {
    currentWorkbook.withValue(workbook){
      val applyed = actions.reverse.foldLeft(workbook)((wb,ac) => ac(wb))
      postActions.reverse.foldLeft(applyed)((wb,ac) => ac(wb))
    }
  }

  def addAction(action: Workbook => Workbook) = {
    actions = action :: actions
  }
  def addPostActions(action: Workbook => Workbook) = {
    postActions = action :: postActions
  }


  def ++(next: Project) = {
    val p = new EmptyProject()
    p.actions = this.actions ++ next.actions
    p.postActions = this.postActions ++ next.postActions
    p
  }





}

class EmptyProject extends Project{

}