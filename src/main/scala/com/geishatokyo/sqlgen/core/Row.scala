package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Row(val parent: Sheet,val rowIndex : Int) {

  val _cells = parent._cells(rowIndex)


  def apply(index: Int): Cell = {
    _cells(index)
  }
  def apply(name: String): Cell = {
    val index = parent.columnIndexOf(name)
    if(index < 0) {
      throw SQLGenException.atSheet(parent,s"Header${name} not found")
    }
    apply(index)
  }

  def cells = _cells

  def header(name: String) = parent.header(name)
  def header(index: Int) = parent.header(index)
  def getHeader(name: String) = parent.getHeader(name)

  /**
    * コード上見やすくするためのもの
    * @param name
    * @return
    */
  def cell(name: String) = apply(name)

  def address = s"${parent.address}/row:${rowIndex}"

  def ++(values: Map[String,Any]): Map[String, Any] = {
    parent.headers.map(_.name).zip(_cells).toMap ++ values
  }

}
