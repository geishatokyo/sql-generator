package com.geishatokyo.sqlgen.sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

class Row(val parent : Sheet,val headers : List[ColumnHeader],val cells : List[Cell]) {

  def apply( index : Int) = cells(index)

  def apply(columnName : String) : Cell = {
    val index = headers.indexWhere( h => h.name == columnName)
    if(index < 0){
      throw new HeaderNotFoundException(parent.name, columnName)
    }else{
      cells(index)
    }
  }
  def update(index : Int, value : String) : Unit  = cells(index) := value
  def update(columnName : String,value : String) : Unit = {
    val index = indexOf(columnName)
    if (index >= 0) cells(index) := value
  }

  def unit(index : Int) = CellUnit(headers(index),cells(index))

  def units = headers.zip(cells).map(p => CellUnit(p._1,p._2))

  def indexOf(columnName : String) = {
    headers.indexWhere(h => h.name == columnName)
  }

  def size = cells.size

  def header(index : Int) : ColumnHeader = headers(index)

  def header(columnName : String) : ColumnHeader = {
    headers.find( h => h.name == columnName).getOrElse{
      throw new HeaderNotFoundException(parent.name, columnName)
    }
  }
  def existColumn(columnName : String) = {
    headers.indexWhere( h => h.name == columnName) >= 0
  }

  override def equals(obj: Any): Boolean = {
    obj match{
      case r : Row => {
        r.eq(this) ||
        (r.headers == this.headers &&
        r.cells == this.cells)
      }
      case _ => false
    }
  }

  override def toString: String = {
    cells.mkString(",")
  }

  def replace( columnName : String, conversion : Cell => String) = {
    val index = indexOf(columnName)
    if (index >= 0) cells(index) := conversion(cells(index))
  }

  def copy() = {
    new Row(this.parent,this.headers,this.cells.map(_.copy(this.parent)))
  }

  def rowIndex = {
    parent.indexOf(this)
  }
}
