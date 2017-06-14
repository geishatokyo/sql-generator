package com.geishatokyo.sqlgen.core.metadata

import com.geishatokyo.sqlgen.core.{ColumnMetadata, Metadata, SheetMetadata}

import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/06/14.
  */
trait UpdatableMetadata extends Metadata {

  val values = mutable.Map.empty[String,Any]

  override def updateDynamic(key: String)(v: Any): Unit = {
    values(key) = v
  }

  override def selectDynamic(key: String): Option[Any] = {
    values.get(key) orElse _selectDynamic(key)
  }
  protected def _selectDynamic(key: String): Option[Any]


}

trait UpdatableSheetMetadata extends SheetMetadata {

  val values = mutable.Map.empty[String,Any]

  override def updateDynamic(key: String)(v: Any): Unit = {
    values(key) = v
  }

  override def selectDynamic(key: String): Option[Any] = {
    values.get(key) orElse _selectDynamic(key)
  }
  protected def _selectDynamic(key: String): Option[Any]
}

trait UpdatableColumnMetadata extends ColumnMetadata {
  val values = mutable.Map.empty[String,Any]

  override def updateDynamic(key: String)(v: Any): Unit = {
    values(key) = v
  }

  override def selectDynamic(key: String): Option[Any] = {
    values.get(key) orElse _selectDynamic(key)
  }
  protected def _selectDynamic(key: String): Option[Any]
}