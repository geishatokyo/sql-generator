package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.core.{Cell, DataType, Row, Sheet}

import scala.language.experimental.macros
import scala.util.DynamicVariable


/**
 * Created by takezoux2 on 15/05/04.
 */

class SheetScope{

  private val currentRow = new DynamicVariable[Row](null)

  def row = currentRow.value

  def withRow(row: Row)(func: => Any) = {
    currentRow.withValue(row)(func)
  }

}

class ColumnRef(sheet: Sheet, var columnName : String, sheetScope: SheetScope) {

  def row = {
    val row = sheetScope.row
    if(row == null) throw new Exception("Not in row scope")
    row
  }

  def foreach(func: Cell => Unit) : Unit = {
    if(sheet.hasColumn(columnName)) {
      sheet.column(columnName).cells.foreach(c => sheetScope.withRow(c.row){
        func(c)
      })
    }
  }

  def name : String = columnName
  def name_=(newName: String) = {
    sheet.column(columnName).header.name = newName
    columnName = newName
  }

  def ensureExists() : ColumnRef = {
    if(!sheet.hasColumn(columnName)){
      sheet.addHeader(columnName)
    }
    this
  }

  /**
    * Set value to foreach cell
    * @param e
    */
  def :=(e : => Any) : Unit = {
    ensureExists()
    sheet.rows.foreach(r => {
      sheetScope.withRow(r){
        val c = r(columnName)
        val v = e
        e match{
          case cr: ColumnRef => {
            c.value = r(cr.columnName).value
          }
          case _ => c.value = e
        }
      }
    })
  }

  /**
    * Set value only when cell is empty.
    * @param e
    */
  def ?=(e : => Any) : Unit = {
    ensureExists()
    setIfEmpty(e)
  }

  def map(mapV : Cell => Any) : Unit = {
    foreach(cell => {
      val v = mapV(cell)
      cell.value = v
    })
  }
  def mapIfEmpty(func : Cell => Any) : Unit = {
    foreach(cell => {
      if(cell.isEmpty){
        cell.value = func(cell)
      }
    })
  }
  def setIfEmpty(func : => Any) : Unit = {
    foreach(cell => {
      if(cell.isEmpty){
        val v = func
        v match{
          case cr : ColumnRef => {
            cell.value = cr.row(cr.columnName).value
          }
          case _ => {
            cell.value = v
          }
        }
      }
    })
  }

  def mapTo(convs: (String,Any)*) = {
    val mapping = convs.toMap
    foreach(cell => {
      val v = cell.asString
      val mapped = mapping.getOrElse(v,cell.value)
      cell.value = mapped
    })
  }
  def mapTo(pf: PartialFunction[Any,Any]) = {
    foreach(cell => {
      cell.value = pf(cell.value)
    })
  }

  def mapEnum[E <: Enumeration](e: E) = {
    foreach(cell => {
      if(cell.dataType == DataType.Integer) {
      } else {
        val s = cell.asString
        try{
          cell.value = e.withName(s).id
        } catch{
          case t: Throwable => //変換できないときは無視
        }
      }
    })
  }


  def asString = {
    row(columnName).asString
  }
  def asInt = {
    row(columnName).asLong.toInt
  }
  def asLong = {
    row(columnName).asLong
  }
  def asDouble = {
    row(columnName).asDouble
  }
  def asDate = {
    row(columnName).asJavaTime
  }

  override def toString: String = {
    asString
  }

  def +(any: Any) = {
    row(columnName) + any
  }

  def -(any: Any) = {
    row(columnName) - any
  }

  def *(any: Any) = {
    row(columnName) * any
  }

  def /(any: Any) = {
    row(columnName) / any
  }

  def %(any: Any) = {
    row(columnName) % any
  }

  def isIgnore: Boolean = sheet.header(columnName).isIgnore
  def isIgnore_=(v: Boolean) = sheet.header(columnName).isIgnore = v

  def isId: Boolean = sheet.header(columnName).isId
  def isId_=(v: Boolean) = sheet.header(columnName).isId = v


}
