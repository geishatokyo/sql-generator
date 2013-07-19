package com.geishatokyo.sqlgen.project2

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

  protected val onSheetName = new DynamicVariable[Regex](null)
  protected val currentWorkbook = new DynamicVariable[Workbook](null)
  protected val currentSheet = new DynamicVariable[Sheet](null)
  protected val currentRow = new DynamicVariable[Row](null)

  protected var processes : List[Workbook => Any] = Nil

  def addSheet(sheetName : String) = {
    processes :+=( (w: Workbook) => {
      if (!w.hasSheet(sheetName)){
        w.addSheet(new Sheet(sheetName))
      }
    })
  }

  def newSheet(newSheetName : String) = new NewSheet(newSheetName)


  def onSheet(sheetName : String)( func : => Unit) = {
    onSheetName.withValue( ("^" + Pattern.quote(sheetName) + "$").r){
      func
    }
  }

  def onSheet( sheetNameRegex : Regex)(func : => Unit) = {
    onSheetName.withValue( sheetNameRegex){
      func
    }
  }

  def forColumn(columnName : String) : ColumnMapping = {
    return new ColumnMapping(onSheetName.value, columnName)
  }


  def filterRow( func : Row => Boolean) = {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w.sheets.foreach(s => s.name.value match {
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
  // Reference


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
    new StringOps( v.value)
  }

  case class ColumnAddress(sheetName : Option[String],columnName : String){
    override def toString = currentRow.value.apply(columnName).value

    def toLong = toString().toLong

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
      (currentRow.value.index - 1 until 0 by -1).view.map(index => {
        sheet.row(index)(columnName).value
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
      (currentRow.value.index + 1 until sheet.rowSize).view.map(index => {
        sheet.row(index)(columnName).value
      }).find( cond(_))
    }
    def searchFirstBelow(cond : String => Boolean) = findFirstBelow(cond).get

  }


  case class SheetAddress(sheetName : String){
    def search( cond : Row => Boolean) : Row = {
      currentWorkbook.value.getSheet(sheetName).map(s => {
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
        r(id.name) == v
      })
    }

    def find ( cond : Row => Boolean) : Option[Row] = {
      currentWorkbook.value.getSheet(sheetName).map(s => {
        s.rows.find(cond)
      }).getOrElse(None)
    }


    def findIdIs( v : => String) : Option[Row] = {
      find( r => {
        val id = r.parent.ids(0)
        r(id.name) == v
      })
    }

    def row(rowIndex : Int) = {
      currentWorkbook.value(sheetName).row(rowIndex)
    }
    def column(columnName : String) = {
      currentWorkbook.value(sheetName).column(columnName)
    }



  }

  def useOnly(sheets : SheetAddress*) : Unit = {
    processes :+= ( (w : Workbook) => {
      val names = sheets.map(_.sheetName).toSet
      w.sheets.foreach(s => {
        s.ignore = !names.contains(s.name.value)
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

  def renameTo(newSheetName : String) {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w.sheetsMatchingTo(sheetName).foreach(s => {
        s.name := newSheetName
      })
    })
  }

  def guessColumnType( guess : PartialFunction[String,ColumnType.Value]) = {

    if (onSheetName.value != null){
      val sheetName = onSheetName.value
      processes :+= ((w : Workbook) => {
        w.sheetsMatchingTo(sheetName).foreach(s => {
          s.headers.foreach(h => {
            if (guess.isDefinedAt(h.name)){
              h.columnType = guess(h.name)
            }
          })
        })
      })
    }else{
      processes :+= ((w : Workbook) => {
        w.sheets.foreach(s => {
          s.headers.foreach(h => {
            if (guess.isDefinedAt(h.name)){
              h.columnType = guess(h.name)
            }
          })
        })
      })
    }

  }
  def guessId( guess : String => Boolean) = {
    processes :+= ((w : Workbook) => {
      w.sheets.foreach(s => {
        val ids = s.headers.filter(h => {
          guess(h.name)
        })

        s.replaceIds(ids.map(_.name.value) :_*)
      })
    })
  }





  class ColumnMapping(sheetNameRegex : Regex,columnName : String) {

    var condition : Option[Row => Boolean] = None

    private val _always = (_ : Row) => true
    private val _ifEmpty = {
      (r : Row) => {
        val v = r(columnName).asString
        v == null || v.length == 0
      }
    }

    def map( func : String => String) : ColumnMapping = {
      mapOrSet(func,condition.getOrElse(_always))
    }

    def set( v : => String) : ColumnMapping = {
      mapOrSet(s => v, condition.getOrElse(_ifEmpty))
    }

    private def mapOrSet(func : String => String, condition : Row => Boolean) = {
      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          currentSheet.withValue(sheet){
            if(!sheet.existColumn(columnName)){
              sheet.addEmptyColumn(columnName)
            }
            sheet.rows.foreach( r => {
              currentRow.withValue(r){
                if (condition(r)){
                  val c = r(columnName)
                  c := func(c.value)
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
      condition = Some(r => func( r(columnName)))
      this
    }

    def ifEmpty = {
      condition = Some(_ifEmpty)
      this
    }

    def renameTo( newName : String ) = {

      processes :+=( (w : Workbook) => {
        w.sheetsMatchingTo(sheetNameRegex).foreach(sheet => {
          sheet.header(columnName).name := newName
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
    def extractFrom(sheetName : String)(columns : String*) = {
      processes :+=( (w : Workbook) => {
        val s = w(sheetName)

        if (!w.hasSheet(newSheetName)){
          w.addSheet(new Sheet(newSheetName))
        }
        val newSheet = w(newSheetName)

        columns.foreach(c => {
          val c2 = s.column(c)
          newSheet.addColumn(c2)
        })
      })
    }
    def extractThenFilter(sheetName : String)(columns : String*)(filterFunc : Row => Boolean) = {
      processes :+=( (w : Workbook) => {
        val s = w(sheetName)

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

      })
    }
    def copy(sheetName : String) : Unit = {
      processes :+=( (w : Workbook) => {
        val s = w(sheetName)
        val s2 = s.copy()
        s2.name := newSheetName
        w.addSheet(s2)
      })
    }
    def copyThenModify(sheetName : String )( modify : Sheet => Sheet) : Unit = {
      processes :+=( (w : Workbook) => {
        val s = w(sheetName)
        val s2 = s.copy()
        s2.name := newSheetName
        w.addSheet(modify(s2))
      })
    }
  }

}

class PlainProject extends Project{

}






