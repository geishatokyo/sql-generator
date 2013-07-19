package com.geishatokyo.sqlgen.sheet

import util.matching.Regex

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

class Workbook extends scala.collection.mutable.Map[String,Sheet]{

  var name : String = "Workbook1"

  protected var _sheets : List[Sheet] = Nil

  def sheets : List[Sheet] = _sheets
  protected def sheets_=( v : List[Sheet]) = _sheets = v


  def +=(kv: (String, Sheet)) = {
    addSheet(kv._2)
    this
  }

  def -=(key: String) = {
    deleteSheet(key)
    this
  }

  def get(key: String) = sheets.find(_.name =~= name)

  def iterator = _sheets.map(s => s.name.value -> s).iterator



  def apply(index : Int) : Sheet  = sheets(index)
  override def apply(name : String) : Sheet = getSheet(name).getOrElse(
    throw new SheetNotFoundException(name)
  )
  def getSheet(name : String) = sheets.find(_.name =~= name)

  def hasSheet(name : String) = sheets.exists(_.name =~= name)

  def sheetsMatchingTo(sheetNameRegex : Regex) = {
    sheets.filter( s => sheetNameRegex.findFirstIn(s.name.value).isDefined)
  }

  def foreachSheet[T]( func : Sheet => T) : List[T] = {
    sheets.map(func(_))
  }

  def addSheet(sheet : Sheet) = {
    sheets = sheets :+ sheet
  }

  def addSheets(sheets : List[Sheet]) = {
    this.sheets = this.sheets ::: sheets
  }


  /**
   * Replace sheet.
   * @param sheet
   */
  def replaceSheet(sheet : Sheet) = {
    deleteSheet(sheet.name.value)
    addSheet(sheet)
  }

  def deleteSheet(sheetName : String) = {
    getSheet(sheetName) match{
      case Some(_) => {
        sheets = sheets.filterNot(s => s.name =~= sheetName)
        true
      }
      case _ => false
    }
  }

  def copy() = {
    val wb = new Workbook()
    wb.name = name
    wb.sheets = sheets.map(_.copy())
    wb
  }

  def copyWithoutHistory() = {
    val wb = new Workbook()
    wb.name = name
    wb.sheets = sheets.map(_.copyWithoutHistory())
    wb
  }

  override def toString: String = {
    """WorkbookName:%s
%s
    """.stripMargin.format(name,sheets.mkString("\n"))

  }
}
