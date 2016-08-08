package com.geishatokyo.sqlgen.project3.refs

import com.geishatokyo.sqlgen.sheet.{Cell, Column, Row, Sheet}

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros


/**
 * Created by takezoux2 on 15/05/04.
 */

object ColumnRef{


  def replaceToForeach(c: Context)(e : c.Expr[Any]) : c.Expr[Unit] = {
    import c.universe._


    val tree = q"""${c.prefix}.foreach{ implicit cell =>
         ${e}
       }
     """

    c.Expr(tree)

  }




}

class ColumnRef(sheet: Sheet, var columnName : String) {

  def foreach(func: Cell => Unit) : Unit = {
    sheet.column(columnName).cells.foreach(func)
  }

  def name : String = columnName
  def name_=(newName: String)(implicit sheet : Sheet) = {
    sheet.column(columnName).header.name = newName
    columnName = newName
  }

  def :=(e : => Any) : Unit = macro ColumnRef.replaceToForeach

  def asString(implicit cell : Cell) = {
    cell.row(columnName).asString
  }
  def asString(implicit row: Row) = {
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
