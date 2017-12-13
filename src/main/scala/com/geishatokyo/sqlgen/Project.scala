package com.geishatokyo.sqlgen

import java.text.SimpleDateFormat
import java.time.{LocalDate, LocalTime, ZoneId, ZonedDateTime}
import java.util.Date

import com.geishatokyo.sqlgen.core.{Row, Sheet, Workbook}
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.process.Context
import com.geishatokyo.sqlgen.query.{Query, WorkbookSearcher}

import scala.util.DynamicVariable
import scala.util.matching.Regex

/**
 * Created by takezoux2 on 15/05/04.
 */
trait Project{

  protected val currentWorkbook = new DynamicVariable[Workbook](null)
  protected val currentContext = new DynamicVariable[Context](null)

  protected val currentSheet = new DynamicVariable[Sheet](null)

  val sheetScope = new SheetScope

  def context = currentContext.value

  /**
    * 現在のスコープのSheetを取得
    * @return
    */
  def sheet = {
    val s = currentSheet.value
    if(s == null) {
      throw new Exception("Not sheet scope")
    }
    s
  }

  /**
    * 現在のスコープのWorkbookを取得
    * @return
    */
  def workbook = {
    val w = currentWorkbook.value
    if(w == null){
      throw new Exception("Not workbook scope")
    }
    w
  }

  /**
    * 指定した名前のシートを取得
    * @param name
    * @return
    */
  def sheet(name : String): Sheet = {
    val wb = workbook
    if(wb.contains(name)) {
      wb(name)
    } else{
      context.get(Context.Import) match{
        case Some(importWbs) => {
          importWbs.find(_.contains(name)) match{
            case Some(wb) => wb(name)
            case None => {
              throw new Exception(s"Sheet:${name} not found")
            }
          }
        }
        case None => {
          throw new Exception(s"Sheet:${name} not found")

        }
      }
    }
  }

  def isSheetExists(name: String): Boolean = {
    val wb = workbook
    if(wb.contains(name)) {
      true
    } else{
      context.get(Context.Import) match{
        case Some(importWbs) => {
          importWbs.find(_.contains(name)) match{
            case Some(wb) => true
            case None => {
              false
            }
          }
        }
        case None => {
          false
        }
      }
    }
  }

  /**
    * 指定した名前のシートを追加
    * すでに存在する場合は何も起きない
    * Sheetスコープ内で使用した場合、即座にSheetを取得可能
    * @param sheetName
    */
  def addSheet(sheetName: String) = {
    if(currentSheet.value == null) {
      addAction(wb => {
        if (!wb.contains(sheetName)) {
          wb.addSheet(new Sheet(wb, sheetName))
        }
        wb
      })
    } else {
      val wb = this.workbook
      if (!wb.contains(sheetName)) {
        wb.addSheet(new Sheet(wb, sheetName))
      } else {
        wb(sheetName)
      }
    }
  }


  /**
    * 現在のスコープのSheetのカラムへの参照を取得する
    * @param name
    * @return
    */
  def column(name : String) : ColumnRef = {
    new ColumnRef(sheet,name,sheetScope)
  }

  /**
    * 現在のスコープのSheetの全行を取得
    * @return
    */
  def rows = {
    sheet.rows
  }
  /**
    * 現在のスコープのSheetの全列を取得
    * @return
    */
  def columns = {
    sheet.columns
  }

  /**
    * 前処理を追加
    * @param func
    */
  def before( func: => Unit) = {
    addPreActions(w => {
      func
      w
    })
  }

  /**
    * 後処理を追加
    * @param func
    */
  def after( func: => Unit) = {
    addPostActions(w => {
      func
      w
    })
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
      r(idColumn.name) == id
    })
  }


  private val workbookSearcher = new WorkbookSearcher()
  /**
    * Queryを使用して、全参照からRowを探す
    * @param q
    * @return
    */
  def select(q: Query) : List[Row] = {
    workbookSearcher.findRows(workbook, q) ++
      (context.get(Context.Import) match{
      case Some(wbs) => wbs.flatMap(workbookSearcher.findRows(_, q))
      case None => Nil
    })
  }

  /**
    * Queryを使用して、はじめに見つかったRowを取得する
    * @param q
    * @return
    */
  def selectOne(q: Query) : Row = {
    workbookSearcher.findFirstRow(workbook, q) orElse {
      context.get(Context.Import) match{
        case Some(wbs) => wbs.view.map(workbookSearcher.findFirstRow(_,q)).
          find(_.isDefined).flatten
        case None => None
      }
    } getOrElse {
      throw SQLGenException.atSheet(sheet, s"No rows found by query:${q}")
    }
  }
  def exists(q: Query) : Boolean = {
    select(q).length > 0
  }


  protected var preActions : List[(Workbook => Workbook)] = Nil
  protected var actions : List[(Workbook => Workbook)] = Nil
  protected var postActions : List[Workbook => Workbook] = Nil


  def onAllSheet(action: => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheets.foreach(sheet => {
        currentSheet.withValue(sheet) {
          action
        }
      })
      wb
    }
    this.actions = func :: this.actions
  }

  def onSheet(sheetName: String)(action : => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.getSheet(sheetName).foreach(sheet => {
        currentSheet.withValue(sheet){action}
      })
      wb
    }
    this.actions =  func :: actions
  }

  def onSheet(sheetMatch: Regex)(action: => Any) : Unit = {
    val func = (wb: Workbook) => {
      wb.sheetsMatchingTo(sheetMatch).foreach(sheet => {
        currentSheet.withValue(sheet){action}
      })
      wb
    }
    this.actions = func :: this.actions
  }

  /**
    * 条件をチェックし、条件を満たしていない場合、ログにWarningメッセージを出す
    * @param validationFunc
    * @param message
    */
  def warn(validationFunc: => Boolean, message: String) = {
    def showWarnMessage() = {
      val address = if(sheetScope != null && sheetScope.row != null) {
        sheetScope.row.address
      } else if(currentSheet.value != null){
        currentSheet.value.address
      } else if(currentWorkbook.value != null) {
        currentWorkbook.value.name
      } else {
        "--"
      }

      Logger.log("!Werning! " + address + " :" + message)
    }
    if(!validationFunc) {
      showWarnMessage()
    }
  }

  def validate(validationFunc: => Boolean): Unit = {
    validate(validationFunc, "Invalid data")
  }
  def validate(validationFunc: => Boolean, message: String): Unit = {

    def throwSQLGenException(t: Throwable) = {
      if(sheetScope != null && sheetScope.row != null) {
        throw SQLGenException.atRow(sheetScope.row,message,t)
      } else if(currentSheet.value != null){
        throw SQLGenException.atSheet(currentSheet.value, message,t)
      } else if(currentWorkbook.value != null) {
        throw SQLGenException.atWorkbook(currentWorkbook.value, message,t)
      } else {
        throw SQLGenException.apply(message,t)
      }
    }


    try{
      val success = validationFunc
      if(!success) {
        throwSQLGenException(null)
      }
    } catch{
      case e: SQLGenException => throw e
      case t: Throwable => {
        throwSQLGenException(t)
      }
    }
  }


  /**
    * 現在のスコープのSheetを無視設定にする
    * @return
    */
  def ignore() = {
    sheet.isIgnore
  }



  def apply(context: Context,workbook : Workbook) = {
    currentWorkbook.withValue(workbook) {
      currentContext.withValue(context) {
        val endPre = preActions.reverse.foldLeft(workbook)((wb,ac) => ac(wb))
        val applied = actions.reverse.foldLeft(endPre)((wb, ac) => ac(wb))
        postActions.reverse.foldLeft(applied)((wb, ac) => ac(wb))
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
    * 現在時刻を取得する
    * @return
    */
  def now = {
    ZonedDateTime.now()
  }

  /**
    * 今日の00:00:00を取得
    *
    * @return
    */
  def today = {
    ZonedDateTime.of(
      LocalDate.now(),
      LocalTime.MIN,
      ZoneId.systemDefault()
    )
  }


  implicit class SheetOps(sheet: Sheet) {
    def renameColumns(maps: (String,String) *) = {
      maps.foreach(conv => {
        sheet.headers.find(_.name == conv._1).foreach(h => {
          h.name = conv._2
        })
      })
    }
  }

  def createSheet(sheetName: String)(initFunc: Sheet => Unit) = {

    def createSheetAction() = {
      if(!workbook.contains(sheetName)) {
        val sheet = workbook.addSheet(sheetName)
        currentSheet.withValue(sheet){
          initFunc(sheet)
        }
      }
    }

    if(currentWorkbook.value != null) {
      createSheetAction()
    } else {
      before({createSheetAction()})
    }

  }


}



class EmptyProject extends Project{

}