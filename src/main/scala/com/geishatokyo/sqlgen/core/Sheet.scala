package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException

import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Sheet(private var _name: String) {

  private[core] var _parent : Workbook = null
  def parent = _parent

  def name = _name
  def name_=(newName: String) = {
    if(_parent != null){
      _parent.changeSheetName(this,newName)
    }
    _name = newName
  }

  var isIgnore = false

  private[core] var _headers : Array[Header] = Array.empty
  private[core] var _cells : Array[Array[Cell]] = Array.empty

  private[core] var _rows : Array[Row] = Array.empty
  private[core] var _columns: Array[Column] = Array.empty

  def headers = _headers
  def columnSize = _headers.size
  def rowSize = _cells.size
  def rows = _rows
  def columns = _columns

  val note = mutable.Map.empty[String,Any]

  def ids = {
    columns.filter(_.header.isId)
  }

  def apply(rowIndex: Int, columnIndex: Int): Cell = {
    _cells(rowIndex)(columnIndex)
  }
  def update(rowIndex: Int, columnIndex: Int, v: Any) = {
    apply(rowIndex, columnIndex) := v
  }

  def columnIndexOf(headerName: String) : Int = {
    _headers.indexWhere(_.name == headerName)
  }
  def header(headerName : String) = {
    getHeader(headerName).getOrElse{
      throw new SQLGenException(s"Header:${headerName} not found in Sheet:${name}")
    }
  }


  def header(index: Int) = {
    headers(index)
  }

  def column(name: String) = {
    columns(columnIndexOf(name))
  }

  def hasColumn(name: String) = {
    columns.exists(_.header.name == name)
  }

  def getHeader(headerName: String) : Option[Header] = {
    headers.find(_.name == headerName)
  }

  def addRow(values: Any*): Row = {

    if(values.size != _headers.size){
      throw new SQLGenException(s"Row length must be ${_headers.size} but was ${values.size}")
    }
    val rowIndex = rows.size
    val row: Seq[Cell] = values.zipWithIndex.map{
      case (c : Cell,columnIndex) => Cell(this,rowIndex, columnIndex, c.variable)
      case (a, columnIndex) => Cell(this, rowIndex, columnIndex, a)
    }

    _cells = this._cells.+:(row.toArray)
    recalculate()
    _rows.last
  }

  private def recalculate() = {
    _cells.zipWithIndex.foreach {
      case (row, rowIndex) => row.zipWithIndex.foreach {
        case (cell, columnIndex) => {
          cell._rowIndex = rowIndex
          cell._columnIndex = columnIndex
        }
      }
    }
    _columns = (0 until columnSize).map(index => new Column(this,index)).toArray
    _rows = (0 until rowSize).map(index => new Row(this,index)).toArray
  }


  def addHeader(headerName: String): Column = {
    if(_headers.exists(_.name == headerName)){
      throw new SQLGenException(s"Header:${headerName} already exists")
    }
    val columnIndex = _headers.length
    val h = new Header(headerName)
    _headers = _headers :+ h
    _cells = _cells.zipWithIndex.map({
      case (row, rowIndex) => row :+ Cell(this,rowIndex, columnIndex, null)
    })
    recalculate()
    columns.last
  }

  def addHeaders(headerNames: String*): Array[Column] = {

    headerNames.find(this.hasColumn(_)) match{
      case Some(name) => {
        throw new SQLGenException(s"Header:${name} already exists")
      }
      case None => {}
    }

    headerNames.foreach(headerName => {
      val columnIndex = _headers.length
      val h = new Header(headerName)
      _headers = _headers :+ h
      _cells = _cells.zipWithIndex.map({
        case (row, rowIndex) => row :+ Cell(this,rowIndex, columnIndex, null)
      })
    })

    recalculate()
    columns.takeRight(headerNames.size)
  }



  def copy() = {
    val c = new Sheet(name)
  }






}
