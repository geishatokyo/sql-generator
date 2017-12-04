package com.geishatokyo.sqlgen.meta

/**
  * Created by takezoux2 on 2017/07/05.
  */
case class Metadata(sheetMetas: List[SheetMeta], caseSensitive: Boolean = false) {

  private val sheetMap = {
    if(caseSensitive) {
      sheetMetas.map(sm => sm.name -> sm).toMap
    }else {
      sheetMetas.map(sm => sm.name.toUpperCase() -> sm).toMap
    }
  }

  def getSheetMeta(name: String) = {
    if(caseSensitive) sheetMap.get(name)
    else sheetMap.get(name.toUpperCase)
  }


}

object Metadata {
  val AutoClass = "#Auto"
  val Empty = Metadata(Nil)
}


case class SheetMeta(name: String,
                     columnMetas: List[ColumnMeta],
                     caseSensitive: Boolean = false) {

  var className: String = name
  var primaryIndex : List[String] = List("id")


  private val columnMap = {
    if(caseSensitive) {
      columnMetas.map(cm => cm.name -> cm).toMap
    }else {
      columnMetas.map(cm => cm.name.toUpperCase -> cm).toMap
    }
  }

  def getColumnMeta(name: String) = {
    if(caseSensitive) {
      columnMap.get(name)
    } else {
      columnMap.get(name.toUpperCase)
    }
  }



}

case class ColumnMeta(name: String) {

  var className: String = Metadata.AutoClass
  var isIgnore: Boolean = false

}
