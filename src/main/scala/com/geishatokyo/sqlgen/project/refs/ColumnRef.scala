package com.geishatokyo.sqlgen.project.refs

import com.geishatokyo.sqlgen.sheet.{Cell, Column, Row, Sheet}

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.util.DynamicVariable


/**
 * Created by takezoux2 on 15/05/04.
 */

object ColumnRef{


  def replaceToForeach(c: Context)(e : c.Expr[Any]) : c.Expr[Unit] = {
    import c.universe._

    println(e)
    val tree = q"""${c.prefix}.foreach{ implicit cell : com.geishatokyo.sqlgen.sheet.Cell =>
         cell.value = ${e.tree}
       }
     """

    println(tree)
    c.Expr(tree)

  }




}

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
    sheet.column(columnName).cells.foreach(func)
  }

  def name : String = columnName
  def name_=(newName: String)(implicit sheet : Sheet) = {
    sheet.column(columnName).header.name = newName
    columnName = newName
  }

  def :=(e : => Any) : Unit = {

    if(!sheet.existColumn(columnName)){
      sheet.addColumns(columnName)
    }
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

  def asString = {
    row(columnName).asString
  }

  def map(mapV : Cell => Any) : Unit = {
    foreach(cell => {
      val v = mapV(cell)
      cell.value = v
    })
  }

  def ignore : Unit = {
    sheet.getColumn(columnName).foreach(c => {
      c.header.output_? = false
    })
  }





}
