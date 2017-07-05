package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Row(val parent: Sheet,val rowIndex : Int) {

  val _cells = parent._cells(rowIndex)


  def apply(index: Int): Cell = {
    _cells(index)
  }
  def apply(name: String): Cell = {
    apply(parent.columnIndexOf(name))
  }

  def cells = _cells

  def header(name: String) = parent.header(name)
  def header(index: Int) = parent.header(index)
  def getHeader(name: String) = parent.getHeader(name)



}
