package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/06/11.
  */
trait ColumnInfoAccessor {

  def isId: Boolean
  def columnType: DataType
  def isIgnore: Boolean
  def isUnique: Boolean
  def defaultValue: Any

}
