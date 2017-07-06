package com.geishatokyo.sqlgen.meta

/**
  * Created by takezoux2 on 2017/07/05.
  */
case class Metadata(sheetMetas: List[SheetMeta]) {

}


case class SheetMeta(name: String,
                     columnMetas: List[ColumnMeta]) {

  var className: String = name
  var primaryIndex : List[String] = List("id")

}

case class ColumnMeta(name: String) {

  var className: String = Metadata.AutoClass
  var isRequre: Boolean = true

}

object Metadata {
  val AutoClass = "#Auto"
}