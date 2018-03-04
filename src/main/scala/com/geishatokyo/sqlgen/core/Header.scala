package com.geishatokyo.sqlgen.core

import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Header(var name: String) {

  private[core] var _parent : Sheet = null

  def parent = _parent

  def column = parent.column(name)


  var isId = false

  val note = mutable.Map.empty[String,Any]

  /**
    * カラムの型情報を示す。
    * 通常はカラム毎に型が推測されるため、使用されていない
    */
  var columnType: Option[String] = None

  var isIgnore = false


  def copy(parent: Sheet) = {
    val h = new Header(name)
    h._parent = parent
    h.isId = this.isId
    this.note.foreach(v => h.note(v._1) = v._2)
    h.columnType = this.columnType
    h.isIgnore = this.isIgnore
    h
  }

}
