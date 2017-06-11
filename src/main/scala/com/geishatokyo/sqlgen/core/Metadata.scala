package com.geishatokyo.sqlgen.core


import scala.language.dynamics

/**
  * Created by takezoux2 on 2017/05/22.
  */
class Metadata extends Dynamic {


  def getSheetMetadata(name: String) : SheetMetadata = {
    new SheetMetadata()
  }

}

class SheetMetadata extends Dynamic{
  def updateDynamic(key: String)(v : Any) = {

  }
  def selectDynamic(key: String) = {
    List("aaa")
  }

  def getColumnMetadata(name: String) : ColumnMetadata = {
    new ColumnMetadata()
  }

}

class ColumnMetadata extends Dynamic{
  def updateDynamic(key: String)(v : Any) = {

  }
  def selectDynamic(key: String): Option[Any] = {
    Some("hoge")
  }
}
