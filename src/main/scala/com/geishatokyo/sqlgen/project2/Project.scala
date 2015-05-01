package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.SQLGenException

import util.DynamicVariable
import com.geishatokyo.sqlgen.sheet._
import sun.rmi.server.InactiveGroupException
import collection.immutable.{StringOps, StringLike, ListMap}
import scala.Some
import util.matching.Regex
import java.util.regex.Pattern

/**
 * 
 * User: takeshita
 * DateTime: 13/07/11 21:46
 */
trait Project extends Function1[Workbook,Workbook] {

  object ScopeLevel extends Enumeration{
    val None = Value
    val Workbook = Value
    val Sheet = Value
    val Row = Value
    val Cell = Value
  }

  protected var scopeLevel = ScopeLevel.None
  protected val currentWorkbook = new DynamicVariable[Workbook](null)
  protected val currentSheet = new DynamicVariable[Sheet](null)
  protected val currentRow = new DynamicVariable[Row](null)
  protected val currentCell = new DynamicVariable[Cell](null)

  def sheet = currentSheet.value
  def workbook = currentWorkbook.value
  def row = currentRow.value
  def cell = currentCell.value


  protected var processes : List[Workbook => Any] = Nil

  def workbookLevelFunc( func : Workbook => Any) = {
    processes :+= func
    func
  }
  def sheetLevelFunc(selector : Workbook => List[Sheet],func : Sheet => Any) = {
    val f : Workbook => Any = w => {
      selector(w).foreach(s => {
        currentSheet.withValue(s){
          scopeLevel = ScopeLevel.Sheet
          func(s)
        }
      })
    }
    workbookLevelFunc(f)
    f
  }


  def rowLevelFunc(selector : Workbook => List[Sheet],func : Row => Any) = {
    sheetLevelFunc(selector , s => {
      s.rows.foreach(r => {
        currentRow.withValue(r) {
          scopeLevel = ScopeLevel.Row
          func(r)
        }
      })
    })
  }

  def onSheetOrWorkbookLevel(func : Sheet => Any) = {
    scopeLevel match{
      case ScopeLevel.None => {
        throwScopeException("workbook or sheet")
      }
      case ScopeLevel.Workbook => {
        workbook.sheets.foreach(func)
      }
      case _ => {
        func(sheet)
      }
    }
  }

  def onSheetLevel(func : Sheet => Any) = {
    scopeLevel match{
      case ScopeLevel.None => {
        throwScopeException("sheet")
      }
      case ScopeLevel.Workbook => {
        throwScopeException("Sheet")
      }
      case _ => {
        func(sheet)
      }
    }
  }

  def withSheet(sheet : Sheet)(func : => Unit) = {
    scopeLevel match{
      case ScopeLevel.Workbook => {
        currentSheet.withValue(sheet){
          scopeLevel = ScopeLevel.Sheet
          func
        }
      }
      case _ => {
        throwScopeException("Workbook")
      }
    }
  }
  def withRow(row : Row)( func : => Unit) = {
    scopeLevel match{
      case ScopeLevel.Sheet => {
        currentRow.withValue(row){
          scopeLevel = ScopeLevel.Row
          func
        }
      }
      case _ => {
        throwScopeException("Sheet")
      }
    }
  }
  def withCell(cell : Cell)( func : => Unit) = {
    scopeLevel match{
      case ScopeLevel.Row => {
        currentCell.withValue(cell){
          scopeLevel = ScopeLevel.Cell
          func
        }
      }
      case _ => {
        throwScopeException("Row")
      }
    }
  }

  def addSheet(sheetName : String) = {
    workbookLevelFunc(w => {
      if(!w.hasSheet(sheetName)){
        w.addSheet(new Sheet(sheetName))
      }
    })
  }

  def newSheet(newSheetName : String) = new NewSheet(newSheetName)

  @inline
  def throwScopeException(scope : String) = {
    throw new SQLGenException(s"Must use in ${scope} scope")
  }

  def renameTo(newSheetName : String) = {
    onSheetLevel(sheet => sheet.name = newSheetName)
  }

  def guessColumnType( guess : PartialFunction[String,ColumnType.Value]) = {
    onSheetOrWorkbookLevel(s => {
      s.headers.foreach(h => {
        if (guess.isDefinedAt(h.name)){
          h.columnType = guess(h.name)
        }
      })
    })
  }
  def guessId( guess : String => Boolean) = {
    onSheetOrWorkbookLevel(s => {
      val ids = s.headers.filter(h => {
        guess(h.name)
      })
      if(ids.size > 0){
        s.replaceIds(ids.map(_.name) :_*)
      }
    })
  }


  def onSheet(sheetName : String)( func : => Unit) : Unit = {
    onSheet(("^" + Pattern.quote(sheetName) + "$").r)(func)
  }

  def onSheet( sheetNameRegex : Regex)(func : => Unit) : Unit = {
    sheetLevelFunc(workbook => {
      workbook.sheets.filter(sheet => {
        sheetNameRegex.findFirstMatchIn(sheet.name).isDefined
      })
    },sheet => {
      func
    })
  }

  // ##########  Use inside of onSheet function. ##################

  def onColumn(columnName : String)(func : => Any) = {
    onSheetLevel(sheet => {
      val column = sheet.column(columnName)
      sheet.rows.foreach(row => {
        withRow(row){
          val cell = row(columnName)
          withCell(cell){
            func
          }
        }
      })
    })
  }
  def column(columnName : String) : ColumnMapping = {
    new ColumnMapping(columnName.r)
  }

  def forColumn(columnName : String) : ColumnMapping = {
    return new ColumnMapping(onSheetName.value, columnName)
  }


  def filterRow( func : Row => Boolean) = {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w.sheets.foreach(s => s.name match {
        case sheetName() => {
          currentSheet.withValue(s){
            for (i <- ((s.rowSize -1 ) to 0 by -1)){
              val row = s.row(i)
              currentRow.withValue(row){
                if (!func(s.row(i))){
                  s.deleteRow(i)
                }
              }
            }
          }
        }
        case _ =>
      })
    })
  }

  def foreachRow( func : Row => Unit) = {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w.sheets.foreach(s => s.name match {
        case sheetName() => {
          currentSheet.withValue(s){
            for (i <- ((s.rowSize -1 ) to 0 by -1)){
              val row = s.row(i)
              currentRow.withValue(row){
                func(s.row(i))
              }
            }
          }
        }
        case _ =>
      })
    })
  }



  def validate(func : Row => Boolean) = {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w.sheets.foreach(s => s.name match {
        case sheetName() => {
          currentSheet.withValue(s){
            s.rows.foreach(row => {
              if(!func(row)){
                throw new Exception("Validation error at %s:%s".format(sheetName,row.rowIndex))
              }
            })
          }
        }
        case _ =>
      })
    })
  }



  // ############## Use inside of processing function. #####################


  /**
   * Use only in condition functions
   * @param columnName
   * @return
   */
  def column(columnName : String) = {
    new ColumnAddress(None,columnName)
  }

  def sheet(sheetName : String) = {
    SheetAddress(sheetName)
  }


  implicit def columnAddressToString(ca : ColumnAddress) = {
    ca.toString()
  }
  implicit def columnAddressToStringOps(ca : ColumnAddress) = {
    new StringOps(ca.toString())
  }

  implicit def cellToString( v : Cell) = {
    v.value
  }
  implicit def cellToStringOps( v : Cell) = {
    new StringOps( v.asString)
  }

  case class ColumnAddress(sheetName : Option[String],columnName : String){
    override def toString = cell.asString

    def cell = currentRow.value(columnName)

    def toLong = cell.asLong
    def asLong = cell.asLong
    def toInt = cell.asInt
    def asInt = cell.asInt
    def toDouble = cell.asDouble
    def asDouble = cell.asDouble
    def asString = toString
    def s = toString
    def i = toInt
    def l = toLong
    def d = toDouble


    def +( ca : ColumnAddress) : String = {
      (this.cell.asLongOp,ca.cell.asLongOp) match{
        case (Some(v1),Some(v2)) => (v1 + v2).toString
        case _ => this.toString + ca.toString
      }
    }
    def -(ca : ColumnAddress) : Long = this.l + ca.l
    def *(ca : ColumnAddress) : Long = this.toLong + ca.toLong
    def /(ca : ColumnAddress) : Double = this.d + ca.d

    def +( s: String): String = this.toString + s

    def +(v : Int) : Int = this.i + v
    def -(v : Int) : Int = this.i - v
    def *( v : Int) : Int = this.i * v
    def /( v : Int) : Int = this.i / v


    def +(v : Long) : Long = this.l + v
    def -(v : Long): Long = this.l - v
    def *( v : Long) : Long= this.l * v
    def /( v : Long): Long = this.l / v


    def +(v : Double) : Double = this.d + v
    def -(v : Double): Double = this.d - v
    def *( v : Double) : Double= this.d * v
    def /( v : Double): Double = this.d / v

    def ^( v : Int) : Long = Math.pow(this.d,v).toLong
    def ^( v : Double) : Double = Math.pow(this.d,v)

    def at (sheetAddress : SheetAddress) = {
      ColumnAddress(Some(sheetAddress.sheetName),columnName)
    }

    def apply( rowIndex : Int) = {
      currentSheet.value.column(columnName).cells(rowIndex)
    }

    /**
     * Find nearest cell value which row index is lower than this.
     * @param cond
     * @return
     */
    def findFirstAbove(cond : String => Boolean) : Option[String] = {
      val sheet = currentSheet.value
      (currentRow.value.rowIndex - 1 to 0 by -1).view.map(index => {
        sheet.row(index)(columnName).asString
      }).find( cond(_))
    }
    def searchFirstAbove(cond : String => Boolean) = findFirstAbove(cond).get
    /**
     * Find nearest cell value which row index is higher than this.
     * @param cond
     * @return
     */
    def findFirstBelow(cond : String => Boolean): Option[String] = {
      val sheet = currentSheet.value
      (currentRow.value.rowIndex + 1 until sheet.rowSize).view.map(index => {
        sheet.row(index)(columnName).asString
      }).find( cond(_))
    }

    def searchFirstBelow(cond : String => Boolean) = findFirstBelow(cond).get

    def validate( func : String => Boolean) = {
      currentSheet.value.rows.foreach( row => {
        if(!func(row(columnName).asString)){
          throw new Exception("Validation error at %s@%s:%s".format(sheetName.get,columnName,row.rowIndex))
        }
      })

    }

  }


  case class SheetAddress(sheetName : String){
    def search( cond : Row => Boolean) : Row = {
      substance.map(s => {
        s.rows.find(cond).getOrElse({
          throw new Exception("Match row is not found in Sheet:" + sheetName + ".")
        })
      }).getOrElse({
        throw new Exception("Try to find match row, but Sheet:" + sheetName + " is not found.")
      })
    }
    def searchIdIs( v: => String) : Row = {
      search( r => {
        val id = r.parent.ids(0)
        r(id.name).asString == v
      })
    }

    /**
     * 現在の対象シートを取得
     * @return
     */
    def substance = currentWorkbook.value.get(sheetName)
    def get = currentWorkbook.value(sheetName)

    def find ( cond : Row => Boolean) : Option[Row] = {
      substance.map(s => {
        s.rows.find(cond)
      }).getOrElse(None)
    }


    def findIdIs( v : => String) : Option[Row] = {
      find( r => {
        val id = r.parent.ids(0)
        r(id.name).asString == v
      })
    }

    def indexOf( cond : Row => Boolean) : Int = {
      substance.map( _.rows.indexOf(cond)).getOrElse(-1)
    }

    def row(rowIndex : Int) = {
      get.row(rowIndex)
    }
    def column(columnName : String) = {
      get.column(columnName)
    }

    def rowSize = {
      substance.map(_.rowSize).getOrElse(0)
    }
    def columnSize = {
      substance.map(_.columnSize).getOrElse(0)
    }

    def foreachRow[T]( func : Row => T) : List[T] = {
      substance.map(_.rows.map(func)).getOrElse(Nil)
    }

    def validate( func : Row => Boolean) = {
      substance.foreach(_.rows.foreach( row => {
        if(!func(row)){
          throw new Exception("Validation error at %s:%s".format(sheetName,row.rowIndex))
        }
      }))
    }

    /**
     *  Append new row
     * @param row
     */
    def +=( row : List[Any]) = {
      get.addRow(row.map(_.toString))
    }

    /**
     * Append new column
     * @param column
     */
    def +|=( column : (String,List[Any])) = {
      get.addColumn(column._1,column._2.map(_.toString))
    }


  }

  // ############### Can use outside of functions. ##############

  def useOnly(sheets : SheetAddress*) : Unit = {
    processes :+= ( (w : Workbook) => {
      val names = sheets.map(_.sheetName).toSet
      w.sheets.foreach(s => {
        s.ignore = !names.contains(s.name)
      })
    })
  }

  def ignore(sa : SheetAddress) : Unit = {
    processes :+= ( (w : Workbook) => {
      w.getSheet(sa.sheetName).foreach(s => {
        s.ignore = true
      })
    })
  }
  def ignore(ca : ColumnAddress) : Unit = {

    val sheetNameRegex = {
      if( onSheetName.value == null){
        ".*".r
      }else{
        onSheetName.value
      }
    }
    processes :+= ( (w : Workbook) => {
      if (ca.sheetName.isDefined){
        w.getSheet(ca.sheetName.get).foreach( s => {
          s.header(ca.columnName).output_? = false
        })
      }else{
        w.sheetsMatchingTo(sheetNameRegex).foreach( s => {
          s.header(ca.columnName).output_? = false
        })
      }
    })

  }



  class ColumnRef(columnName : String){
    def :=( value : => Any) = {
      set(value)
    }
    def map(func : String => Any) = {

    }
    def set( value : => Any) = {

    }

    def renameTo(newColumnName : String) = {
      onColumn(columnName){

      }
    }
  }

  class ConditionController(proc : Workbook => Any){

    def ifEmpty = {

    }

    def when( condition : => Boolean) = {

    }

    def always = {

    }

  }



  class ColumnMapping(columnName : String) {

    var condition : Option[Row => Boolean] = None

    private val _always = (_ : Row) => true
    private val _ifEmpty = {
      (r : Row) => {
        val v = r(columnName).asString
        v == null || v.length == 0
      }
    }

    def map( func : String => Any) : ColumnMapping = {
      mapOrSet(func,_always)
    }

    def set( v : => Any) : ColumnMapping = {
      mapOrSet(s => v,_ifEmpty)
    }

    private def mapOrSet(func : String => Any, defaultCond : Row => Boolean) = {
      processes :+=( (w : Workbook) => {
        val condition = this.condition.getOrElse(defaultCond)
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          currentSheet.withValue(sheet){
            if(!sheet.existColumn(columnName)){
              sheet.addEmptyColumn(columnName)
            }
            sheet.rows.foreach( r => {
              currentRow.withValue(r){
                if (condition(r)){
                  val c = r(columnName)
                  c := {
                    func(c.asString)
                  }
                }
              }
            })
          }})
        })
      this
    }

    def always : ColumnMapping = {
      condition = Some(_always)
      this
    }

    def when( func : String => Boolean) = {
      condition = Some(r => func( r(columnName).asString))
      this
    }

    def ifEmpty = {
      condition = Some(_ifEmpty)
      this
    }

    def renameTo( newName : String ) = {

      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          sheet.header(columnName).name = newName
        })
      })
      this
    }

    def ignore = {
      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          sheet.header(columnName).output_? = false
        })
      })
      this
    }

    def type_=(columnType : ColumnType.Value) = {
      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          sheet.header(columnName).columnType = columnType
        })
      })
      this
    }

    def isId = {
      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          sheet.replaceIds(columnName)
        })
      })
      this
    }


  }



  def apply(workbook : Workbook) : Workbook = {
    val w = workbook.copy()

    currentWorkbook.withValue(w){
      processes.foreach( v => {
        v(w)
      })
    }

    w
  }

  def modifyRows(sheetName : String)( func : Row => Any) = {
    processes :+=( (w : Workbook) => {
      w.getSheet(sheetName).foreach(s => {
        currentSheet.withValue(s){
          s.rows.foreach( r => {
            currentRow.withValue(r){
              func(r)
            }
          })
        }
      })

    })
  }


  def +(p : Project) = {
    val merge = new PlainProject()
    merge.processes = this.processes ++ p.processes
    merge
  }

  class NewSheet(newSheetName : String) {

    private def switchExec( processFunc : Workbook => Any) = {

      if(currentWorkbook.value != null){
        //すでに実行プロセスの場合
        val w = currentWorkbook.value
        processFunc(w)
      }else{
        processes :+= processFunc
      }
    }

    def createEmpty(columnNames : String*) = {
      switchExec( (w : Workbook) => {
        if(!w.hasSheet(newSheetName)){
          val sheet = new Sheet(newSheetName)
          sheet.addColumns(columnNames :_*)
          w.addSheet(sheet)
        } else {
          val sheet = w(newSheetName)

          val columns = sheet.columns.map(_.columnName).sorted

          if(columns != columnNames.sorted){
            throw new Exception("Can't create same name sheet!")
          }
        }
      })
    }

    def extractFrom(sheetName : String)(columns : String*) = {
      switchExec((w : Workbook) => {
        w.get(sheetName) match{
          case Some(s) => {
            if (!w.hasSheet(newSheetName)){
              w.addSheet(new Sheet(newSheetName))
            }
            val newSheet = w(newSheetName)

            columns.foreach(c => {
              val c2 = s.column(c)
              newSheet.addColumn(c2)
            })
          }
          case None =>
        }

      })
    }
    def extractThenFilter(sheetName : String)(columns : String*)(filterFunc : Row => Boolean) = {
      switchExec( (w : Workbook) => {
        w.get(sheetName) match{
          case Some(s) => {
            val newSheet = new Sheet(newSheetName)

            columns.foreach(c => {
              val c2 = s.column(c)
              newSheet.addColumn(c2)
            })
            val resultSheet = newSheet.copyEmpty()
            newSheet.rows.foreach(r => {
              if(filterFunc(r)){
                resultSheet.addRow(r)
              }
            })
            w.addSheet(resultSheet)
          }
          case None =>
        }

      })
    }
    def copy(sheetName : String) : Unit = {
      switchExec( (w : Workbook) => {
        w.get(sheetName) match{
          case Some(s) => {
            val s = w(sheetName)
            val s2 = s.copy()
            s2.name = newSheetName
            w.addSheet(s2)
          }
          case None => {
          }
        }
      })
    }
    def copyThenModify(sheetName : String )( modify : Sheet => Sheet) : Unit = {
      switchExec( (w : Workbook) => {
        w.get(sheetName) match{
          case Some(s) => {
            val s = w(sheetName)
            val s2 = s.copy()
            s2.name = newSheetName
            w.addSheet(modify(s2))
          }
          case None => {
          }
        }
      })
    }
  }

}

class PlainProject extends Project{

}






