package com.geishatokyo.sqlgen.core


import scala.language.dynamics

/**
  * Created by takezoux2 on 2017/05/22.
  */
trait Metadata extends Dynamic {

  def updateDynamic(key: String)(v : Any): Unit
  def selectDynamic(key: String): Option[Any]

  def getSheetMetadata(name: String) : SheetMetadata

}

trait SheetMetadata extends Dynamic{
  def updateDynamic(key: String)(v : Any): Unit
  def selectDynamic(key: String): Option[Any]

  def getColumnMetadata(name: String): ColumnMetadata
}

object ColumnMetadataKeys {
  val IsID = "isId"
  val IsUnique = "isUnique"
  val ColumnType = "columnType"
  val IsIgnore  ="isIgnore"
}

trait ColumnMetadata extends Dynamic{

  def isId = selectDynamic(ColumnMetadataKeys.IsID) map{ _ == true} getOrElse(false)
  def isUnique = selectDynamic(ColumnMetadataKeys.IsUnique) orElse selectDynamic(ColumnMetadataKeys.IsID) map {
    _ == true
  } getOrElse false


  def columntype = selectDynamic(ColumnMetadataKeys.ColumnType).map(_.toString)

  def isIgnore = selectDynamic(ColumnMetadataKeys.IsIgnore)

  def updateDynamic(key: String)(v : Any): Unit
  def selectDynamic(key: String): Option[Any]
}
