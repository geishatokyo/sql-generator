package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Column(val parent: Sheet, columnIndex: Int) {

  val column = columnIndex

  def name = header.name
  def apply(index: Int) = parent._cells(index)(columnIndex)
  def header = parent._headers(columnIndex)

  def cells = parent._cells.iterator.map(_(columnIndex))



}
