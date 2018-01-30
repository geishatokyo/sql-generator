package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.setting.WorkbookConfSupport

import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Sheet(private[core] var _parent : Workbook,
            private var _name: String) extends WorkbookConfSupport {

  def parent = _parent

  def config = parent.config


  def name = _name
  def name_=(newName: String) = {
    _name = newName
  }

  def address = {
    if(parent != null) {
      s"${parent.name}/${this.name}"
    } else {
      s"UnknownWB/${this.name}"
    }
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

  def rows_=(rows: Array[Row]): Unit = {
    if(this._rows == rows) {
      checkHeaders(rows)
      _cells = Array.empty
      _rows = Array.empty
      _columns = Array.empty
      addRows(rows:_*)
    }
  }
  def rows_=(rows: Seq[Row]) = {
    checkHeaders(rows)
    _cells = Array.empty
    _rows = Array.empty
    _columns = Array.empty
    addRows(rows:_*)
  }


  val note = mutable.Map.empty[String,Any]

  def ids = {
    columns.filter(_.header.isId)
  }

  def apply(columnIndex: Int, rowIndex: Int): Cell = {
    _cells(rowIndex)(columnIndex)
  }
  def update(columnIndex: Int, rowIndex: Int, v: Any) = {
    apply(rowIndex, columnIndex) := v
  }

  def columnIndexOf(headerName: String) : Int = {
    _headers.indexWhere(h => eqStr(h.name,headerName))
  }
  def header(headerName : String) = {
    getHeader(headerName).getOrElse{
      throw SQLGenException.atSheet(this, s"Header:${headerName} not found")
    }
  }


  def header(index: Int) = {
    headers(index)
  }

  def column(name: String) = {
    columns(columnIndexOf(name))
  }

  def hasColumn(name: String) = {
    columns.exists(h => eqStr(h.header.name, name))
  }

  def getHeader(headerName: String) : Option[Header] = {
    headers.find(h => eqStr(h.name, headerName))
  }

  def addRow(row: Row): Row = {

    if(row.parent.headers != this.headers) {
      val notExists = this.headers.filter(h => {
        !row.parent.headers.exists(h2 => {
          eqStr(h.name, h2.name  )
        })
      })
      if(notExists.size > 0) {
        throw SQLGenException.atSheet(this, s"Row header is not match.Not founds:${notExists.map(_.name).mkString(",")}")
      }
    }
    appendRow(row)
    recalculate()
    _rows.last
  }

  def addRow(row: Map[String,Any]): Row = {
    val notExists = this.headers.filter(h => {
      !row.exists(t => {
        eqStr(t._1, h.name)
      })
    })
    if(notExists.size > 0) {
      throw SQLGenException.atSheet(this,s"Row header is not match.Not founds:${notExists.map(_.name).mkString(",")}")
    }
    val values = this.headers.map(h => row.find(t => eqStr(t._1, h.name)).get._2)
    addRow(values:_*)
  }

  def addRows(rows: Row*) : Unit = {
    checkHeaders(rows)
    rows.foreach(row => appendRow(row))
    recalculate()
  }

  private def checkHeaders(rows: Seq[Row]) = {

    val headers = rows.map(_.parent.headers).distinct

    val allGreen = headers.forall(header => {
      header == this.headers ||
        header.forall(h => this.headers.exists(header => eqStr(header.name, h.name)))
    })
    if(!allGreen) {
      throw SQLGenException.atSheet(this, s"Row header is not match")
    }
  }

  private def appendRow(row: Row) = {

    val rowIndex = _cells.size
    val values = this.headers.zipWithIndex.map(h => {
      Cell(this,rowIndex, h._2, row(h._1.name).variable)
    })
    _cells = this._cells :+ (values.toArray)
  }

  def addRow(values: Any*): Row = {

    if(values.size != _headers.size){
      throw SQLGenException.atSheet(this, s"Row length must be ${_headers.size} but was ${values.size}")
    }
    val rowIndex = rows.size
    val row: Seq[Cell] = values.zipWithIndex.map{
      case (c : Cell,columnIndex) => Cell(this,rowIndex, columnIndex, c.variable)
      case (a, columnIndex) => Cell(this, rowIndex, columnIndex, a)
    }

    _cells = this._cells :+ (row.toArray)
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
    if(_headers.exists(h => eqStr(h.name, headerName))){
      throw SQLGenException.atSheet(this, s"Header:${headerName} already exists")
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
        throw SQLGenException.atSheet(this, s"Header:${name} already exists")
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




  def copyTo(wb: Workbook): Sheet = {
    if(wb.contains(this.name)) {
      throw SQLGenException.atWorkbook(wb, s"Workbook:${wb.name} already contains Sheet:${name}")
    }
    val newSheet = wb.addSheet(name)

    newSheet._headers = this._headers.map(_.copy(newSheet))
    this.note.foreach(t => {
      newSheet.note(t._1) = t._2
    })
    newSheet.addRows(this.rows:_*)
    newSheet.isIgnore = this.isIgnore

    newSheet

  }






}
