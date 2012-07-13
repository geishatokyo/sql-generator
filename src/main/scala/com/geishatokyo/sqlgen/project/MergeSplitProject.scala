package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.sheet.{Row, Workbook}
import com.geishatokyo.sqlgen.{Project, SQLGenException}

/**
 *
 * User: takeshita
 * Create: 12/07/13 11:35
 */

trait MergeSplitProject extends Project with SheetScope with SheetAddress {

  import MergeSplitProject._


  abstract override protected def beginScope(sheetName: String) {
    super.beginScope(sheetName)
  }
  abstract override protected def endScope(sheetName: String) {
    super.endScope(sheetName)
  }

  private def scopeError( name : String) = {
    throw new SQLGenException("%s must invoke inside sheet scope".format(name))
  }
  protected var reversedTasks : List[MergeSplitTask] = Nil
  protected def addTask( t : MergeSplitTask) = reversedTasks = t :: reversedTasks
  def tasks = reversedTasks.reverse


  object merge{
    def sheet(sheetName : String) = {
      new SheetMerge(sheetName)
    }

  }

  object split{
    def sheet(sheetName : String) = {
      new SheetSplit(sheetName)
    }
  }

  object modify{

    def column(columnName : String) = {
      new ColumnModifyWithAddress(columnName)
    }
    def column(columnAddress : ColumnAddress) = {
      new ColumnModifyWithAddress(columnAddress)
    }

    def sheet(sheetName : String) = {
      new SheetModify(sheetName)
    }
  }
  class SheetMerge(sheetName : String){
    def from(columnsList : List[ColumnAddress]*) = {
      addTask(MergeSheetTask(sheetName , columnsList.flatten.toList))
    }

    def select( columns : String*) = {
      new Select(sheetName,columns.toList.map(c => c -> c))
    }
    def selectM( columns : (String,String)*) = {
      new Select(sheetName,columns.toList)
    }

  }
  class Select(sheetName : String , mergeColumns : List[(String,String)]){

    def from( fromSheet : String) = {
      new Object{
        def where( columnName : String) = {
          new Object{
            def isSame = {
              addTask(MergeSelectTask(sheetName,mergeColumns,fromSheet, (my,your) => {
                my(columnName) =~= your(columnName).value
              }))
            }

            def is(compColumnName : String) = {
              addTask(MergeSelectTask(sheetName,mergeColumns,fromSheet, (my,your) => {
                my(columnName) =~= your(compColumnName).value
              }))
            }
          }
        }
        def where( func : (Row,Row) => Boolean) = {
          addTask(MergeSelectTask(sheetName,mergeColumns,fromSheet,func))
        }
      }

    }



  }


  class SheetSplit(fromSheetName : String){

    def into( columnsList : List[ColumnAddress]* ) = {
      addTask(SplitSheetTask(fromSheetName,columnsList.flatten.toList))
    }

  }

  class SheetModify(sheetName : String){

    def rename( newName : String) {
      addTask(RenameSheet(sheetName,newName))
    }

    def delete {
      addTask(DeleteSheet(sheetName))
    }

  }

  class ColumnModify(columnName : String) {

    def myAddress = {
      ColumnAddress(scopedSheet,columnName)
    }

    def rename( name : String) {
      if(!inScope_?) scopeError("column rename")
      addTask(RenameColumnTask(myAddress,name))
    }

    def convert( func : String => String) {
      if(!inScope_?) scopeError("column convert")
      addTask(ConvertColumnTask(myAddress,func))
    }

    def copyTo( columnAddresses : List[ColumnAddress] *) {
      if(!inScope_?) scopeError("column copyTo")
      addTask(CopyTo(myAddress,columnAddresses.flatten.toList))
    }

    def copyFrom( columnAddress : ColumnAddress) {
      if(!inScope_?) scopeError("column copyFrom")
      addTask(CopyTo(columnAddress,List(myAddress)))
    }

    def delete {
      if(!inScope_?) scopeError("column delete")
      addTask(DeleteColumn(myAddress))

    }

    def ignore {
      if(!inScope_?) scopeError("column ignore")
      addTask(IgnoreColumn(myAddress,true))

    }
    def notIgnore {
      if(!inScope_?) scopeError("column not ignore")
      addTask(IgnoreColumn(myAddress,false))

    }

  }

  class ColumnModifyWithAddress(myAddress : ColumnAddress) {

    def rename( name : String) {
      addTask(RenameColumnTask(myAddress,name))
    }

    def convert( func : String => String) {
      addTask(ConvertColumnTask(myAddress,func))
    }

    def copyTo( columnAddresses : List[ColumnAddress] *) {
      addTask(CopyTo(myAddress,columnAddresses.flatten.toList))
    }

    def copyFrom( columnAddress : ColumnAddress) {
      addTask(CopyTo(columnAddress,List(myAddress)))
    }

    def delete {
      addTask(DeleteColumn(myAddress))
    }

    def ignore {
      addTask(IgnoreColumn(myAddress,true))
    }
    def notIgnore{
      addTask(IgnoreColumn(myAddress,false))
    }
  }





}

object MergeSplitProject{

  trait MergeSplitTask
  case class MergeSheetTask( sheetName : String, columns : List[ColumnAddress]) extends MergeSplitTask
  case class SplitSheetTask( sheetName : String, columns : List[ColumnAddress]) extends MergeSplitTask
  case class RenameSheet(sheetName : String, newName : String) extends MergeSplitTask
  case class DeleteSheet(sheetName : String) extends MergeSplitTask
  case class RenameColumnTask(ca : ColumnAddress, newName : String) extends MergeSplitTask
  case class ConvertColumnTask(ca : ColumnAddress, func : String => String) extends MergeSplitTask
  case class DeleteColumn(ca : ColumnAddress) extends MergeSplitTask
  case class IgnoreColumn(ca : ColumnAddress,ignore : Boolean) extends MergeSplitTask
  case class CopyTo( from : ColumnAddress, to : List[ColumnAddress]) extends MergeSplitTask
  case class MergeSelectTask(sheetName : String,
                             columns : List[(String,String)],
                             fromSheetName : String, func : (Row,Row) => Boolean) extends MergeSplitTask
}
