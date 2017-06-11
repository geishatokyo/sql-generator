package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Column(val parent: Sheet, columnIndex: Int) {

  val column = columnIndex


  def apply(index: Int) = parent._cells(index)(columnIndex)
  def header = parent._headers(columnIndex)

  def cells = parent._cells.iterator.map(_(columnIndex))


  def metadata = parent.metadata.getColumnMetadata(header.name)


}
