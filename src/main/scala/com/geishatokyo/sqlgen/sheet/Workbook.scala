package com.geishatokyo.sqlgen.sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

class Workbook {

  var name : String = "Workbook1"

  protected var sheets : List[Sheet] = Nil

  def apply(index : Int) : Sheet  = sheets(index)
  def apply(name : String) : Sheet = getSheet(name).getOrElse(
    throw new SheetNotFoundException(name)
  )
  def getSheet(name : String) = sheets.find(_.name =~= name)

  def foreachSheet[T]( func : Sheet => T) : List[T] = {
    sheets.map(func(_))
  }

  def addSheet(sheet : Sheet) = {
    sheets = sheets :+ sheet
  }

  def addSheets(sheets : List[Sheet]) = {
    this.sheets = this.sheets ::: sheets
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

}
