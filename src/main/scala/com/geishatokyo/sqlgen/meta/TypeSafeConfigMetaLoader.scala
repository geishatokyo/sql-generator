package com.geishatokyo.sqlgen.meta

import java.io.{InputStream, InputStreamReader}

import com.geishatokyo.sqlgen.SQLGenException
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.JavaConverters._

/**
  * Created by takezoux2 on 2017/07/05.
  */
class TypeSafeConfigMetaLoader extends MetaLoader{
  override def load(input: InputStream): Metadata = {
    try {
      val conf = ConfigFactory.parseReader(new InputStreamReader(input, "utf-8"))
      loadConfig(conf)
    }finally{
      input.close()
    }
  }

  val SheetProp = "sheets"
  val NameProp = "name"
  val ColumnProp = "columns"
  val ClassNameProp = "className"


  def loadConfig(config: Config): Metadata = {
    if(config.hasPath(SheetProp) && config.hasPath(NameProp)) {
      val name = config.getString(NameProp)
      Metadata(name, config.getConfigList(SheetProp).asScala.map(conf => {
        loadSheet(conf)
      }).toList)
    } else {
      if(!config.hasPath(NameProp)) {
        throw SQLGenException("Metadata needs name property")
      } else {
        throw SQLGenException("Metadata needs sheets property")
      }
    }
  }

  private def loadSheet(config: Config): SheetMeta = {

    val name = config.getString(NameProp)
    val s = if(config.hasPath(ColumnProp)) {
      val cols = config.getConfigList(ColumnProp).asScala.map(loadColumn(_)).toList
      SheetMeta(name, cols)
    } else {
      SheetMeta(name, Nil)
    }

    setString(config, ClassNameProp, s.className = _)
    setStringList(config, "primaryIndex", s.primaryIndex = _)
    s
  }
  private def loadColumn(config: Config): ColumnMeta = {

    val name = config.getString(NameProp)

    val c = ColumnMeta(name)

    setString(config, ClassNameProp, c.className = _)
    setBool(config, "isIgnore", c.isIgnore = _)

    c
  }

  private def setString(config: Config, key: String, func: String => Unit): Unit = {
    if(config.hasPath(key)) {
      func(config.getString(key))
    }
  }

  private def setBool(config: Config, key: String, func: Boolean => Unit): Unit = {
    if(config.hasPath(key)) {
      func(config.getBoolean(key))
    }
  }

  private def setStringList(config: Config, key: String, func : List[String] => Unit): Unit = {
    if(config.hasPath(key)) {
      func(config.getStringList(key).asScala.toList)
    }
  }

}
