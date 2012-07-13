package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.SQLGenException

/**
 *
 * User: takeshita
 * Create: 12/07/13 12:06
 */

trait SheetAddress extends SheetScope {

  implicit def columnAddress(s : String) = {
    column(s)
  }
  implicit def toCAList(ca : ColumnAddress) = List(ca)


  def column(columnName : String) : ColumnAddress = {
    val c = new ColumnAddress(addressScope,columnName)
    if (_addressScope.isDefined){
      _generatedColumns :+= c
    }
    c
  }

  protected var _generatedColumns : List[ColumnAddress] = Nil
  protected var _addressScope : Option[String] = None

  def addressScope = _addressScope.getOrElse(scopedSheet)

  def at(sheetName : String)(func : => Any) : List[ColumnAddress] = {
    this.synchronized{
      _generatedColumns = Nil
      val old = _addressScope
      _addressScope = Some(sheetName)
      func
      _addressScope = old
      val generated = _generatedColumns
      _generatedColumns = Nil
      generated
    }
  }

}

case class ColumnAddress(sheetName : String, columnName : String) {

  var workbookName : Option[String] = None


  def @@(sheetName : String) = at(sheetName)

  def at(sheetName : String) = {
    new ColumnAddress(sheetName,columnName)
  }
  def in(workbookName : String) = {
    val ca = new ColumnAddress(sheetName,columnName)
    ca.workbookName = Some(workbookName)
    ca
  }

  override def toString: String = columnName + "@" + sheetName
}
