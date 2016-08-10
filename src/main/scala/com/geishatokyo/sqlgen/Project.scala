package com.geishatokyo.sqlgen

import java.text.SimpleDateFormat
import java.util.Date

import com.geishatokyo.sqlgen.project.flow.{DataProcessor, InputData}
import com.geishatokyo.sqlgen.project.refs.{ColumnRef, SheetScope}
import com.geishatokyo.sqlgen.sheet.{Row, Sheet, Workbook}

import scala.util.DynamicVariable
import scala.util.matching.Regex

/**
 * Created by takezoux2 on 15/05/04.
 */
trait Project extends DataProcessor{

  protected val currentWorkbook = new DynamicVariable[Workbook](null)
  protected val currentContext = new DynamicVariable[Context](null)

  protected val currentSheet = new DynamicVariable[Sheet](null)

  val sheetScope = new SheetScope

  def context = currentContext.value

  def sheet = {
    val s = currentSheet.value
    if(s == null) {
      throw new Exception("Not sheet scope")
    }
    s
  }
  def workbook = {
    val w = currentWorkbook.value
    if(w == null){
      throw new Exception("Not workbook scope")
    }
    w
  }

  def sheet(name : String) = {
    val wb = currentWorkbook.value
    if(wb == null) throw new Exception("Not workbook scope")

    if(wb.contains(name)){
      wb(name)
    }else{
      //参照するワークブックにも含まれているかチェック
      context.references.find(w => {
        w.contains(name)
      }).map(_(name)).getOrElse({
        throw new Exception(s"Sheet:${name} not found")
      })
    }
  }

  def addSheet(sheetName: String) = {
    addAction(wb => {
      if(!wb.contains(sheetName)){
        wb.addSheet(new Sheet(sheetName))
      }
      wb
    })
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

  /**
    * 現在のシートから、指定したIDの行を取得する
    *
    * @param id
    * @return
    */
  def findById(id: Any) : Option[Row] = {
    val idColumn = sheet.ids.headOption.getOrElse(throw new Exception(s"Sheet:${sheet.name} has no ids"))
    rows.find(r => {
      r(idColumn.name) ~== id
    })
  }


  protected var preActions : List[(Workbook => Workbook)] = Nil
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

  def process(inputDatas: List[InputData]) = {
    inputDatas.map(data => {
      val c = data.context
      InputData(c,apply(c,data.workbook))
    })
  }


  def apply(context: Context,workbook : Workbook) = {
    currentWorkbook.withValue(workbook) {
      currentContext.withValue(context) {
        val applyed = actions.reverse.foldLeft(workbook)((wb, ac) => ac(wb))
        postActions.reverse.foldLeft(applyed)((wb, ac) => ac(wb))
      }
    }
  }

  def addAction(action: Workbook => Workbook) = {
    actions = action :: actions
  }

  /**
    * 通常アクションの前に実行されるアクションを追加する
    *
    * @param action
    */
  def addPreActions(action: Workbook => Workbook) = {
    preActions = action :: preActions
  }

  /**
    * 通常アクションの後に実行されるアクションを実行する
    *
    * @param action
    */
  def addPostActions(action: Workbook => Workbook) = {
    postActions = action :: postActions
  }


  def ++(next: Project) = {
    val p = new EmptyProject()
    p.preActions = this.preActions ++ next.preActions
    p.actions = this.actions ++ next.actions
    p.postActions = this.postActions ++ next.postActions
    p
  }

  /**
    * 現時刻のString表現を取得
    * @return
    */
  def now = {
    new SimpleDateFormat("YYYY/MM/DD HH:mm:ss").format(new Date)
  }

  /**
    * 今日の日付のString表現を取得
    * @return
    */
  def today = {
    new SimpleDateFormat("YYYY/MM/DD").format(new Date)
  }



}

class EmptyProject extends Project{

}