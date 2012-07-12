package project

import com.geishatokyo.sqlgen.sheet.ColumnType
import com.geishatokyo.sqlgen.Project
import scala.collection.mutable

/**
 *
 * User: takeshita
 * Create: 12/07/12 12:46
 */

trait BaseProject extends Project with Scope {
  import BaseProject._


  protected var _sheetNameMaps : MapAndPartialFunc[String] = new MapAndPartialFunc({
    case s => s
  })
  def sheetNameMaps : PartialFunction[String,String] = _sheetNameMaps

  protected var _ignoreSheetNames : SetAndPartialFunc = new SetAndPartialFunc({
    case _ => false
  })
  def ignoreSheetNames : PartialFunction[String,Boolean] = _ignoreSheetNames

  protected var _globalColumnNameMaps : MapAndPartialFunc[String] = new MapAndPartialFunc({
    case s => s
  })
  def globalColumnNameMaps : PartialFunction[String,String] = _globalColumnNameMaps

  protected var _globalIgnoreColumns : SetAndPartialFunc = new SetAndPartialFunc({
    case _ => false
  })
  def globalIgnoreColumns : PartialFunction[String,Boolean] = _globalIgnoreColumns

  protected var _globalColumnTypeGuesser : MapAndPartialFunc[ColumnType.Value] =
    new MapAndPartialFunc({
      case _ => ColumnType.Any
    })

  protected var _globalColumnDef : MultiRepository[ColumnDef] = new MultiRepository[ColumnDef]


  def __columnNameMaps : MapAndPartialFunc[String] = _globalColumnNameMaps
  def __ignoreColumns: SetAndPartialFunc = _globalIgnoreColumns
  def __columnTypeGuesser: MapAndPartialFunc[ColumnType.Value] = _globalColumnTypeGuesser
  def __columnDef: MultiRepository[ColumnDef] = _globalColumnDef

  def globalColumnTypeGuesser : PartialFunction[String, ColumnType.Value] = _globalColumnTypeGuesser

  protected var _sheetSettings : List[SheetSetting] = Nil
  def sheetSettings = _sheetSettings

  def apply(sheetName : String) = getOrCreateSheetSetting(sheetName)

  def getSheetSetting(sheetName : String) : Option[SheetSetting] = {
    val sn = sheetName.toLowerCase
    _sheetSettings.find( ss => {
      ss.name.toLowerCase == sn
    })
  }

  def getOrCreateSheetSetting(sheetName : String) : SheetSetting = {
    getSheetSetting(sheetName) match{
      case Some(s) => s
      case None => {
        val s = new SheetSetting(sheetName)
        _sheetSettings :+= s
        s
      }
    }
  }

  def onSheet(sheetName : String)( func : => Any) = {
    val s = getOrCreateSheetSetting(sheetName)
    this.synchronized{
      val oldScope = scope
      scope = s
      beginScope(sheetName)
      func
      endScope(sheetName)
      scope = oldScope
    }
  }
  protected def beginScope( sheetName : String) : Unit = {}
  protected def endScope( sheetName : String) : Unit = {}


  protected var scope : Scope = this


  object ignore {

    def sheet(sheetName : String) = {
      _ignoreSheetNames += sheetName
    }

    def sheets( sheetNames : String*) = {
      _ignoreSheetNames ++= sheetNames
    }

    def sheet(by : ByType) = {
      new Object{
        def pf(_pf : PartialFunction[String,Boolean]) = {
          _ignoreSheetNames += _pf
        }
      }
    }

    def column(columnName : String) : Unit = {
      scope.__ignoreColumns += columnName
    }
    def columns(columnNames : String*) = {
      scope.__ignoreColumns ++= columnNames
    }

    def column( by : ByType) : Unit= {
      new Object{
        def pf( _pf : PartialFunction[String,Boolean]) = {
          scope.__ignoreColumns += _pf
        }
      }
    }
  }

  object map{

    def sheetName(mapping : (String,String)) = {
      _sheetNameMaps += mapping
    }

    def sheetNames(mapping : (String,String)*) = {
      _sheetNameMaps ++= mapping
    }

    def columnName( v : (String,String)) : Unit = {
      scope.__columnNameMaps += v
    }
    def columnNames( vs : (String,String)*) : Unit = {
      scope.__columnNameMaps ++= vs
    }

    def columnName( by : ByType) : Unit = {
      new Object{
        def pf( _pf : PartialFunction[String,String]) = {
          scope.__columnNameMaps += _pf
        }
      }
    }
  }

  object ensure{

    def column(name : String) = {
      new Object{
        def throws(error : ErrorType) = {
          new ThrowErrorDetail(name)
        }

        def exists : Unit = {
          scope.__columnDef +=(name -> Exists(name))
        }
        def set( v : String) = {
          new SetValueDetail(name,v)
        }

        def convert( f : String => String) = {
          scope.__columnDef +=( name -> Convert(name,f))
        }

      }
    }
  }

  class ThrowErrorDetail( columnName : String) {
    def whenNotExists : Unit = {
      scope.__columnDef +=( columnName -> ThrowErrorWhenNotExist(columnName))
    }
    def whenEmpty : Unit = {
      scope.__columnDef +=( columnName -> ThrowErrorWhen(columnName,empty))
    }
    def when( f : String => Boolean) = {
      scope.__columnDef +=( columnName -> ThrowErrorWhen(columnName,f))
    }
  }

  class SetValueDetail(columnName : String, value : String) {

    def whenEmpty = {
      scope.__columnDef +=( columnName -> SetDefaultValue(
        columnName,value,
        empty
      ))
    }
    def when( f : String => Boolean) = {
      scope.__columnDef +=( columnName -> SetDefaultValue(
        columnName,value,
        f
      ))
    }
  }

  val empty : String => Boolean = s => s == null || s.length == 0

  object guess{

    def columnTypes( vs : (String,ColumnType.Value) *) = {
      scope.__columnTypeGuesser ++= vs
    }

    def columnType( v : (String,ColumnType.Value)) = {
      scope.__columnTypeGuesser += v
    }

    def columnType( by : ByType) = {
      new Object{
        def pf( _pf : PartialFunction[String,ColumnType.Value]) = {
          scope.__columnTypeGuesser += _pf
        }
      }
    }


  }

  object by
  type ByType = by.type
  object be
  type BeType = be.type
  object error
  type ErrorType = error.type



  case class SheetSetting(name : String) extends Scope{

    protected var _ignoreColumns : SetAndPartialFunc = new SetAndPartialFunc
    def ignoreColumns : PartialFunction[String,Boolean] = {
      _ignoreColumns orElse _globalIgnoreColumns
    }

    protected var _columnTypeGuesser : MapAndPartialFunc[ColumnType.Value] =
      new MapAndPartialFunc
    def columnTypeGuesser : PartialFunction[String, ColumnType.Value] = {
      _columnTypeGuesser orElse  _globalColumnTypeGuesser
    }

    protected var _columnDef : MultiRepository[ColumnDef] = new MultiRepository[ColumnDef]

    def columnDef = {
      _columnDef.allValues ::: _globalColumnDef.allValues
    }


    protected var _columnNameMaps : MapAndPartialFunc[String] = new MapAndPartialFunc
    def columnNameMaps : PartialFunction[String,String] = _columnNameMaps orElse globalColumnNameMaps


    def __columnNameMaps: MapAndPartialFunc[String] = _columnNameMaps
    def __ignoreColumns: SetAndPartialFunc = _ignoreColumns
    def __columnTypeGuesser: MapAndPartialFunc[ColumnType.Value] = _columnTypeGuesser
    def __columnDef: MultiRepository[ColumnDef] = _columnDef
  }


}

object BaseProject{


  class SetAndPartialFunc(var innerPartialFunc : PartialFunction[String,Boolean])
    extends PartialFunction[String,Boolean] {

    def this() = this(Map.empty)

    var list : Set[String] = Set()

    def +=( name : String) = list = list + name

    def ++=( names : Seq[String]) = list = list ++ names

    def +=( pf : PartialFunction[String,Boolean]) = {
      innerPartialFunc = pf.orElse(innerPartialFunc)
    }

    def apply(v1: String): Boolean = {
      if (list.contains(v1)) true
      else{
        innerPartialFunc(v1)
      }
    }

    def isDefinedAt(x: String): Boolean = {
      list.contains(x) || innerPartialFunc.isDefinedAt(x)
    }
  }


  class MapAndPartialFunc[T](var innerPartialFunc : PartialFunction[String,T])
    extends PartialFunction[String,T] {

    def this() = this(Map.empty)
    var map : Map[String,T] = Map.empty

    def +=( kv : (String,T)) = map = map + kv
    def ++=( kvs : Seq[(String,T)]) = map = map ++ kvs

    def +=( pf : PartialFunction[String,T]) = {
      innerPartialFunc = pf.orElse(innerPartialFunc)
    }

    def apply(v1: String): T = {
      map.getOrElse(v1 , {
        innerPartialFunc(v1)
      })
    }

    def isDefinedAt(x: String): Boolean = {
      map.isDefinedAt(x) || innerPartialFunc.isDefinedAt(x)
    }
  }

  class MultiRepository[T]{

    val multiMap = new mutable.HashMap[String,mutable.Set[T]]() with mutable.MultiMap[String,T]

    def +=( kv : (String,T)) = {
      multiMap.addBinding(kv._1 , kv._2)
    }

    def allValues : List[T] = {
      multiMap.values.flatten.toList
    }
  }

  abstract class ColumnDef(val priority : Int)
  case class Exists(columnName : String) extends ColumnDef(1)
  case class SetDefaultValue(
                              columnName : String, defaultValue : String,
                              when : String => Boolean) extends ColumnDef(2)
  case class Convert(columnName : String, func : String => String) extends ColumnDef(3)
  case class ThrowErrorWhenNotExist(columnName : String) extends ColumnDef(4)
  case class ThrowErrorWhen(columnName : String, when : String => Boolean) extends ColumnDef(5)
}

private[project] trait Scope{
  import BaseProject._
  def __columnNameMaps : MapAndPartialFunc[String]
  def __ignoreColumns : SetAndPartialFunc
  def __columnTypeGuesser : MapAndPartialFunc[ColumnType.Value]
  def __columnDef : MultiRepository[ColumnDef]
}