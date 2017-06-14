package com.geishatokyo.sqlgen.core.metadata

import com.geishatokyo.sqlgen.core.{ColumnMetadata, Metadata, SheetMetadata}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

/**
  * Created by takezoux2 on 2017/06/14.
  */
class TSConfMetadata(config: Config) extends UpdatableMetadata {

  lazy val sheets = {
    val list = config.getConfigList("sheets")
    list.asScala.map(c => {
      val name = c.getString("name")
      name -> new TSConfSheetMetadata(c)
    }).toMap
  }


  override protected def _selectDynamic(key: String): Option[Any] = {
    ValueGetter.get(config, key)
  }

  override def getSheetMetadata(name: String): SheetMetadata = {
    sheets.getOrElse(name, null)
  }
}

object ValueGetter{

  def get(config: Config, key: String) : Option[Any] = {
    if(config.hasPath(key)){
      val c = config.getValue(key)
      Some(config.getAnyRef(key))
    } else {
      None
    }
  }

}

class TSConfSheetMetadata(config: Config) extends UpdatableSheetMetadata {

  lazy val columns = {
    config.getConfigList("columns").asScala.map( c => {
      c.getString("name") -> new TSConfColumnMetadata(config)
    }).toMap
  }

  override protected def _selectDynamic(key: String): Option[Any] = {
    ValueGetter.get(config, key)
  }

  override def getColumnMetadata(name: String): ColumnMetadata = {
    columns(name)
  }
}

class TSConfColumnMetadata(config: Config) extends UpdatableColumnMetadata {

  override protected def _selectDynamic(key: String): Option[Any] = {
    ValueGetter.get(config, key)
  }

}