package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Header(val name: String) {

  private[core] var _parent : Sheet = null

  def parent = _parent

  def column = parent.column(name)


  def isId = _parent


}
