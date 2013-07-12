package com.geishatokyo.sqlgen.sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:04
 */

class Column(val parent : Sheet,val header : ColumnHeader,val cells : List[Cell]) {

  def apply(index : Int) = cells(index).value
  def update(index : Int,value : String) = cells(index) := value

  def unit(index : Int) = CellUnit(header,cells(index))

  def size = cells.size
  def columnName = header.name.value

  override def equals(obj: Any): Boolean = {
    obj match{
      case c : Column => {
        c.eq(this) ||
        (c.header == this.header &&
        c.cells == this.cells)
      }
      case _ => false
    }
  }

  override def toString: String = {
    """Header:%s RowSize:%s""".format(header,size)
  }

  def copy(parent : Sheet) = {
    new Column(parent,header.copy(parent),cells.map(_.copy(parent)))
  }
}
