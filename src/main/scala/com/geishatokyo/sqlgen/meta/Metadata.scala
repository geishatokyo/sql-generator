package com.geishatokyo.sqlgen.meta

/**
  * Created by takezoux2 on 2017/07/05.
  */
case class Metadata(sheetMetas: List[SheetMeta]) {

  private val sheetMap = sheetMetas.map(sm => sm.name -> sm).toMap

  def getSheetMeta(name: String) = sheetMap.get(name)


}

object Metadata {
  val AutoClass = "#Auto"
  val Empty = Metadata(Nil)
}


case class SheetMeta(name: String,
                     columnMetas: List[ColumnMeta]) {

  var className: String = name
  var primaryIndex : List[String] = List("id")


  private val columnMap = columnMetas.map(cm => cm.name -> cm).toMap

  def getColumnMeta(name: String) = {
    columnMap.get(name)
  }



}

case class ColumnMeta(name: String) {

  var className: String = Metadata.AutoClass
  var isRequre: Boolean = true
  var isIgnore: Boolean = false

}
