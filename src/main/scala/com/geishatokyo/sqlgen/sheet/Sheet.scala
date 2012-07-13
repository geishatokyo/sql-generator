package com.geishatokyo.sqlgen.sheet

import com.geishatokyo.sqlgen.SQLGenException


/**
 *
 * User: takeshita
 * Create: 12/07/11 21:05
 */

class Sheet(val name : VersionedValue) {

  def this(sheetName:  String) = this(new VersionedValue(sheetName))
  protected var _headers : List[ColumnHeader] = Nil
  def headers = _headers

  protected var _ids : List[ColumnHeader] = Nil
  def ids = _ids

  protected var cells : List[List[Cell]] = Nil
  protected var rows : List[Row] = Nil
  protected var columns : List[Column] = Nil

  var ignore = false



  protected def cellsToRows() = {
    rows = cells.zipWithIndex.map({
      case (values , index) => new Row(this,index,_headers,values)
    })
  }
  protected def rowsToCells() = {
    cells = rows.map(_.cells)
  }
  protected def cellsToColumns() = {
    columns = _headers.zipWithIndex.map({
      case (header,index) => new Column(this,header, cells.map(_.apply(index)))
    })
  }

  protected def columnsToCells() = {
    if(columns.size == 0){
      cells = Nil
      _headers = Nil
    }else{
      val rowSize = columns(0).cells.size
      cells = (0 until rowSize).map(i => {
        columns.map(c => c.cells(i))
      }).toList
      _headers = columns.map(_.header)
    }
  }

  def apply( row : Int, col : Int) = {
    cells(row)(col).value
  }

  def cellAt(row : Int, col : Int) = {
    cells(row)(col)
  }

  def update(row : Int, col : Int , v : String) = {
    cells(row)(col) := v
  }


  def rowSize = cells.size
  def columnSize = _headers.size

  def foreachRow[T]( func : Row => T) : List[T] = {
    rows.map(func(_))
  }
  def foreachColumn[T](func : Column => T) : List[T] ={
    columns.map(func(_))
  }

  def row(index : Int) = {
    rows(index)
  }

  def column(index : Int) : Column = {
    columns(index)
  }
  def column(columnName : String) : Column = {
    val index = headerIndex(columnName)
    if(index < 0){
      throw new HeaderNotFoundException(name, columnName)
    }else{
      column(index)
    }
  }

  def getColumn(columnName : String) : Option[Column] = {
    val index = headerIndex(columnName)
    if(index < 0){
      None
    }else{
      Some(column(index))
    }
  }

  def existColumn(columnName : String) = {
    headerIndex(columnName) >= 0
  }
  def header(columnName : String) : ColumnHeader = {
    _headers.find( h => h.name =~= columnName).getOrElse{
      throw new HeaderNotFoundException(name, columnName)
    }
  }

  def headerIndex(columnName : String) = {
    _headers.indexWhere( h => h.name =~= columnName)
  }
  def findFirstRowWhere( columnName : String, value : String, caseInsensitive : Boolean = false) : Option[Row] = {
    val index = headerIndex(columnName)
    if (index < 0) return None
    rows.find( r => {
      if (caseInsensitive){
        r.cells(index) =~= value
      }else{
        r(index) == value
      }
    })
  }
  def findRowsWhere(columnName : String, value : String, caseInsensitive : Boolean = false) : List[Row] = {
    val index = headerIndex(columnName)
    if (index < 0) return Nil
    rows.filter( r => {
      if (caseInsensitive){
        r.cells(index) =~= value
      }else{
        r(index) == value
      }
    })
  }
  def findFirstRowWhere( func : Row => Boolean) : Option[Row] = {
    rows.find(func(_))
  }


  def addRow( values : List[String]) = {
    if(values.size != _headers.size) {
      throw new SQLGenException(
        "Column size missmatch.Sheet:%s Passed:%s".format(_headers.size,values.size))
    }
    cells = cells :+ values.map(v => new Cell(this,v))
    cellsToRows()
    cellsToColumns()
  }
  def addRows( rows : List[List[String]]) = {
    if (!rows.forall(row => row.size == _headers.size)){
      throw new SQLGenException(
      "Column size missmatch.SheetRowSize:%s".format(_headers.size))
    }
    cells = cells ::: rows.map( row => row.map(v => new Cell(this,v)))
    cellsToRows()
    cellsToColumns()

  }

  def replaceIds( idColumnNames : String*) = {
    _ids = idColumnNames.map(n => header(n)).toList
  }
  def addEmptyRow() = {
    cells = cells :+ List.fill(_headers.size)(new Cell(this,""))
    cellsToRows()
    cellsToColumns()
  }

  protected def checkColumnExist(columnName : String){
    if (existColumn(columnName)){
      throw new Exception("Column:%s already exists".format(columnName))
    }
  }

  def addColumn(columnName : String,values : List[String]) = {
    if (columnSize > 0 && values.size != cells.size) {
      throw new SQLGenException(
        "Row size missmatch.Sheet:%s Passed:%s".format(cells.size,values.size))
    }
    checkColumnExist(columnName)
    columns = columns :+ new Column(
      this,
      new ColumnHeader(this,new VersionedValue(columnName)),
      values.map(v => new Cell(this,v)))

    columnsToCells
    cellsToRows
  }

  /**
   * Append empty columns
   * @param columnNames
   */
  def addColumns(columnNames : String*) = {
    columnNames.foreach( cn => {
      checkColumnExist(cn)
    })
    val rowSize = this.rowSize
    def genColumn(columnName : String) = {
      new Column(
        this,
        new ColumnHeader(this,new VersionedValue(columnName)),
        List.fill(rowSize)(new Cell(this,""))
      )
    }
    columns = columns ::: columnNames.map(genColumn(_)).toList
    columnsToCells
    cellsToRows
  }

  def overwriteColumn(columnName : String, values : List[String]) = {

    if (columnSize >0 && values.size != cells.size) {
      throw new SQLGenException(
        "Row size missmatch.Sheet:%s Passed:%s".format(cells.size,values.size))
    }
    checkColumnExist(columnName)
    column(columnName).cells.zip(values).foreach({
      case (c,v) => c := v
    })
  }


  def deleteRow(index : Int) : Unit = {
    rows = rows.take(index) ::: rows.drop(index + 1)
    rowsToCells
    cellsToColumns
  }

  def deleteColumn(index : Int) : Boolean = {
    columns = columns.take(index) ::: columns.drop(index + 1)
    columnsToCells
    cellsToRows
    true
  }

  def deleteColumn(columnName : String) : Boolean = {
    val index = headerIndex(columnName)
    if(index < 0){
      //throw new HeaderNotFoundException(name, columnName)
      false
    }else{
      deleteColumn(index)
    }
  }

  def copy() = {
    val newSheet = new Sheet(name.copy())
    newSheet._headers = this.headers.map(_.copy(newSheet))
    newSheet.cells = this.cells.map(row => row.map(_.copy(newSheet)))
    newSheet.cellsToRows
    newSheet.cellsToColumns
    newSheet
  }

  def copyWithoutHistory() = {
    val newSheet = new Sheet(new VersionedValue(name.value))
    newSheet._headers = this.headers.map(h => new ColumnHeader(newSheet,h.name.value))
    newSheet.cells = this.cells.map(row => row.map(r => new Cell(newSheet,r.value)))
    newSheet.cellsToRows
    newSheet.cellsToColumns
    newSheet
  }

  override def toString: String = {
    """Header
%s
Row
%s""".format(headers.mkString(","),cells.map(row => row.map(_.value).mkString(",")).mkString("\r\n"))
  }
}
