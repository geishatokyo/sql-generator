package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.sheet.{Column, CellUnit, Row, Cell}
import com.geishatokyo.sqlgen.Project
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.project.ValidateProject.ValidationTask

/**
 *
 * User: takeshita
 * Create: 12/07/18 11:30
 */

trait ValidateProject extends Project with SheetScope {

  protected var globalValidations : List[ValidationTask] = Nil
  protected var _validations : Map[String,List[ValidationTask]] = Map.empty

  def validations(sheetName : String) = {
    _validations.getOrElse(sheetName,Nil) ::: globalValidations
  }

  abstract override protected def beginScope(sheetName: String) {
    super.beginScope(sheetName)
  }

  abstract override protected def endScope(sheetName: String) {
    super.endScope(sheetName)
  }

  private def  _addValidationTask( t : ValidationTask) = {
    if(inScope_?){
      _validations +=(scopedSheet -> (_validations.getOrElse(scopedSheet,Nil) :+ t))
    }else{
      globalValidations :+= t
    }
  }

  import ValidateProject._

  object validate{

    def eachRows( func : Row => Boolean) = {
      _addValidationTask(ValidateRowTask(func))
    }
    def column(columnName : String) = {
      new ValidateColumn(columnName)
    }

  }

  class ValidateColumn(columnName : String){
    def isNotEmpty = {
      is(notEmpty)
    }

    def is( func : CompositeFunc) {
      _addValidationTask(ValidateColumnTask(columnName,func))
    }

    def is( func : String => Boolean) {
      is(new CompositeFuncImpl(func,""))
    }

    /*def isValidXml = {
      is(validXml)
    }

    def isValidJson = {
      is(validJson)
    }*/

    def isSingleLine = {
      is(singleLine)
    }
  }

}

object ValidateProject{

  val notEmpty = new CompositeFuncImpl(v => {v != null && v.length > 0},"HasEmptyColumn")
//  val validXml = new CompositeFuncImpl(v => {
//    try{
//      XML.loadString("<root>%s</root>".format(v))
//      true
//    }catch{
//      case e : Throwable => false
//    }
//  },"NotValidXML")
//
//  val validJson = new CompositeFuncImpl(v => {
//    try{
//      JSON.parseRaw(v) match{
//        case Some(_) => true
//        case None => false
//      }
//    }catch{
//      case e : Throwable=> false
//    }
//  },"NotValidJson")
  val singleLine = new CompositeFuncImpl(v => {
    v != null && ( !v.contains("\r") && !v.contains("\n"))
  },"NotSingleLine")



  implicit def funcToComposite(f : String => Boolean) = new CompositeFuncImpl(f,"")

  trait CompositeFunc extends Function1[CellUnit,Boolean]{

    def and(func : CompositeFunc) = {
      &&(func)
    }

    def or(func : CompositeFunc) = {
      ||(func)
    }

    def &&(f : CompositeFunc) = {
      new AndCompositeFunc(this,f)
    }

    def ||(f : CompositeFunc) = {
      new OrCompositeFunc(this,f)
    }
  }

  case class CompositeFuncImpl(func : String => Boolean,message : String) extends CompositeFunc{
    def apply(cell : CellUnit): Boolean = {
      if(func(cell.value.asString)) true
      else {
        Logger.log("Validation failed at %s@%s(Message:%s Value:'%s')".format(
          cell.header.name,cell.header.parent.name,message,cell.value
        ))
        false
      }
    }
  }
  class AndCompositeFunc(f1 : CompositeFunc,f2 : CompositeFunc) extends CompositeFunc{
    def apply(v1: CellUnit): Boolean = {
      f1(v1) && f2(v1)
    }
  }
  class OrCompositeFunc(f1 : CompositeFunc,f2 : CompositeFunc) extends CompositeFunc{
    def apply(v1: CellUnit): Boolean = {
      f1(v1) || f2(v1)
    }
  }



  trait ValidationTask
  case class ValidateColumnTask( columnName : String, func : CompositeFunc) extends ValidationTask
  case class ValidateRowTask( func : Row => Boolean) extends ValidationTask




}
