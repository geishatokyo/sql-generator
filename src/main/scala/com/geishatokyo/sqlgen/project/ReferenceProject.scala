package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.{SQLGenException, Project}
import com.geishatokyo.sqlgen.sheet.Row
import com.geishatokyo.sqlgen.project.ReferenceProject.ReferenceOtherSheet

/**
 *
 * User: takeshita
 * Create: 12/09/03 17:11
 */
trait ReferenceProject extends Project with SheetScope {


  def set = {
    new SetColumn()
  }

  protected class SetColumn{

    def column(columnName : String) = {
      new SetFrom(columnName)
    }

  }

  protected class SetFrom(columnName : String){

    def from(sheetName : String) = {
      new SetFromWhere(columnName,sheetName)
    }
  }

  protected class SetFromWhere(columnName : String, sheetName : String) {
    def where( whereFunc : (Row,Row) => Boolean) = {
      new SetFromWhereValue(columnName,sheetName,whereFunc)
    }
  }

  protected class SetFromWhereValue(columnName : String,
                                    sheetName : String ,
                                    whereFunc : (Row,Row) => Boolean){
    def value( valueFunc : Row => String) = {
      new SetFromWhereValueWhen(columnName,sheetName,whereFunc,valueFunc)
    }
  }
  protected class SetFromWhereValueWhen(columnName : String,
                                        sheetName : String ,
                                        whereFunc : (Row,Row) => Boolean,
                                        valueFunc : Row => String){
    def always = {
      when(s => true)

    }

    def whenEmpty = {
      when( s => s == null || s.trim.length == 0)
    }

    def when( f: String => Boolean) = {
      val d = ReferenceOtherSheet(columnName,sheetName,whereFunc,valueFunc,f)
      if(!inScope_?){
        throw new SQLGenException("Reference setting must be declared in sheet scope")
      }
      _referenceColumnSettings +=(scopedSheet -> (d :: _referenceColumnSettings(scopedSheet)))
    }

  }

  private var _referenceColumnSettings : Map[String,List[ReferenceOtherSheet]] = Map.empty.withDefaultValue(Nil)

  def getReferenceSettings(sheetName : String) = _referenceColumnSettings(sheetName)

}

object ReferenceProject{

  case class ReferenceOtherSheet(
                                  columnName : String,
                                  sheetName : String ,
                                  whereFunc : (Row,Row) => Boolean,
                                  valueFunc : Row => String,
                                  whenFunc : String => Boolean
                                  )

}
