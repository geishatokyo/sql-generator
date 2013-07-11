package com.geishatokyo.sqlgen.project2

import util.DynamicVariable
import com.geishatokyo.sqlgen.sheet.{ColumnType, Row, Sheet, Workbook}
import sun.rmi.server.InactiveGroupException
import collection.immutable.ListMap

/**
 * 
 * User: takeshita
 * DateTime: 13/07/11 21:46
 */
trait Project {

  private val onSheetName = new DynamicVariable[String](null)
  private val currentWorkbook = new DynamicVariable[Workbook](null)
  private val currentSheet = new DynamicVariable[Sheet](null)
  private val currentRow = new DynamicVariable[Row](null)

  private var processes : List[Workbook => Any] = Nil

  def addSheet(sheetName : String) = {
    processes :+=( (w: Workbook) => {
      if (!w.hasSheet(sheetName)){
        w.addSheet(new Sheet(sheetName))
      }
    })
  }

  def onSheet(sheetName : String)( func : => Unit) = {
    onSheetName.withValue( sheetName){
      func
    }
  }

  def forColumn(columnName : String) : ColumnMapping = {
    return new ColumnMapping(onSheetName.value, columnName)
  }
  def column(columnName : String) = {
    new ColumnAddress({
      if(onSheetName.value == null){
        None
      }else{
        Some(onSheetName.value)
      }
    }, columnName)
  }

  def sheet(sheetName : String) = {
    SheetAddress(sheetName)
  }

  implicit def columnAddressToString(ca : ColumnAddress) = {
    ca.toString()
  }
  case class ColumnAddress(sheetName : Option[String],columnName : String){
    override def toString = currentRow.value.apply(columnName).value
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


  }


  def ignore(sa : SheetAddress) : Unit = {
    processes :+= ( (w : Workbook) => {
      w.getSheet(sa.sheetName).foreach(s => {
        s.ignore = true
      })
    })
  }
  def ignore(ca : ColumnAddress) : Unit = {

    processes :+= ( (w : Workbook) => {
      if (ca.sheetName.isDefined){
        w.getSheet(ca.sheetName.get).foreach( s => {
          s.header(ca.columnName).output_? = false
        })
      }else{
        w.sheets.foreach( s => {
          s.header(ca.columnName).output_? = false
        })
      }
    })

  }

  def renameTo(newSheetName : String) {
    val sheetName = onSheetName.value
    processes :+=( (w : Workbook) => {
      w(sheetName).name := newSheetName
    })
  }

  def guessColumnType( guess : PartialFunction[String,ColumnType.Value]) = {

    if (onSheetName.value != null){
      val sheetName = onSheetName.value
      processes :+= ((w : Workbook) => {
        w.get(sheetName).foreach(s => {
          s.headers.foreach(h => {
            guess.applyOrElse(h.name.value, (_ : String) => {})
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





  class ColumnMapping(sheetName : String,columnName : String) {

    var condition : Row => Boolean = null

    ifEmpty // default condition

    def map( func : String => String) : ColumnMapping = {
      processes :+=( (w : Workbook) => {
        currentSheet.withValue(w(sheetName)){
          val sheet = w(sheetName)
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

      this
    }

    def set( v : => String) : ColumnMapping = {
      map(s => v)
    }

    def always : ColumnMapping = {
      condition = (_ : Row) => true
      this
    }

    def when( func : String => Boolean) = {
      condition = r => func( r(columnName))
      this
    }

    def ifEmpty = {
      condition = (r => {
        val v = r(columnName).asString
        v == null || v.length == 0
      })
      this
    }

    def renameTo( newName : String ) = {

      processes :+=( (w : Workbook) => {
        val sheet = w(sheetName)
        sheet.header(columnName).name := newName
      })
      this
    }

    def ignore = {
      processes :+=( (w : Workbook) => {
        val sheet = w(sheetName)
        sheet.header(columnName).output_? = false
      })
      this
    }

    def type_=(columnType : ColumnType.Value) = {
      processes :+=( (w : Workbook) => {
        val sheet = w(sheetName)
        sheet.header(columnName).columnType = columnType
      })
      this
    }

    def isId = {
      processes :+=( (w : Workbook) => {
        val sheet = w(sheetName)
        sheet.replaceIds(columnName)
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

  def ++(p : Project) = {
    val merge = new PlainProject()
    merge.processes = this.processes ++ p.processes
    merge
  }

}

class PlainProject extends Project{

}






