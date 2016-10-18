package com.geishatokyo.sqlgen.project.refs

import java.util.Date

import com.geishatokyo.sqlgen.sheet.{Cell, Column, Row, Sheet}

import scala.concurrent.duration.FiniteDuration
import scala.reflect.macros.blackbox.Context
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
    if(sheet.existColumn(columnName)) {
      sheet.column(columnName).cells.foreach(c => sheetScope.withRow(c.row){
        func
      })
    }
  }

  def name : String = columnName
  def name_=(newName: String) = {
    sheet.column(columnName).header.name = newName
    columnName = newName
  }

  def ensureExists() : ColumnRef = {
    if(!sheet.existColumn(columnName)){
      sheet.addColumns(columnName)
    }
    this
  }

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


  def asString = {
    row(columnName).asString
  }
  def asInt = {
    row(columnName).asInt
  }
  def asLong = {
    row(columnName).asLong
  }
  def asDouble = {
    row(columnName).asDouble
  }
  def asDate = {
    row(columnName).asDate
  }

  override def toString: String = {
    asString
  }


  def +(duration: FiniteDuration) = {
    val d = row(columnName).asDate
    new Date(d.getTime + duration.toMillis)
  }
  def -(duration: FiniteDuration) = {
    val d = row(columnName).asDate
    new Date(d.getTime - duration.toMillis)
  }
  def +(s: String) = {
    row(columnName).asString + s
  }
  
  def +(i: Int) = {
    row(columnName).asInt + i
  }
  def -(i: Int) = {
    row(columnName).asInt - i
  }
  def /(i: Int) = {
    row(columnName).asInt / i
  }
  def *(i: Int) = {
    row(columnName).asInt * i
  }
  def ^(i: Int) = {
    Math.pow(row(columnName).asDouble,i).toInt
  }


  def +(i: Double) = {
    row(columnName).asDouble + i
  }
  def -(i: Double) = {
    row(columnName).asDouble - i
  }
  def /(i: Double) = {
    row(columnName).asDouble / i
  }
  def *(i: Double) = {
    row(columnName).asDouble * i
  }
  def ^(i: Double) = {
    Math.pow(row(columnName).asDouble,i)
  }

  def +(i: Long) = {
    row(columnName).asLong + i
  }
  def -(i: Long) = {
    row(columnName).asLong - i
  }
  def /(i: Long) = {
    row(columnName).asLong / i
  }
  def *(i: Long) = {
    row(columnName).asLong * i
  }
  def ^(i: Long) = {
    Math.pow(row(columnName).asDouble,i).toLong
  }
  

  def ignore : Unit = {
    sheet.getColumn(columnName).foreach(c => {
      c.header.output_? = false
    })
  }





}
