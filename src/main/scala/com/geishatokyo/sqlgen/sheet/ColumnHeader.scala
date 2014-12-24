package com.geishatokyo.sqlgen.sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:27
 */

class ColumnHeader(val parent : Sheet,var name : String) {

  var columnType : ColumnType.Value = ColumnType.Any
  var output_? : Boolean = true
  var tag : Any = null

  def copy(newParent : Sheet) = {
    val ch =new ColumnHeader(newParent,name)
    ch.columnType = columnType
    ch.output_? = output_?
    ch
  }

  override def toString: String = {
    """%s(type:%s,output?:%s)""".format(name,columnType,output_?)
  }

  override def equals(obj: Any): Boolean = {
    obj match{
      case ch : ColumnHeader => {
        this.eq(ch) || (
          this.name == ch.name
          )
      }
      case _ => false
    }
  }
}
