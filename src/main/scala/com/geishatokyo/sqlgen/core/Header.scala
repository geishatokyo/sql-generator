package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Header(var name: String) {

  private[core] var _parent : Sheet = null

  def parent = _parent

  def column = parent.column(name)

  def columnInfoAccessor = _parent.parent.actionRepository.getColumnInfoAccessor(column)

  def isId = columnInfoAccessor.isId

  def columnType = columnInfoAccessor.columnType

  def isUnique = columnInfoAccessor.columnType

  def isIgnore = columnInfoAccessor.isIgnore

  def defaultValue = columnInfoAccessor.defaultValue

}
