package com.geishatokyo.sqlgen.process.merge

import com.geishatokyo.sqlgen.process.ProcessProvider
import com.geishatokyo.sqlgen.project.{ColumnAddress, MergeSplitProject}
import com.geishatokyo.sqlgen.process.Proc
import com.geishatokyo.sqlgen.sheet._
import com.geishatokyo.sqlgen.project.MergeSplitProject.MergeSplitTask
import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.project.ColumnAddress
import scala.Some

/**
 *
 * User: takeshita
 * Create: 12/07/13 15:40
 */

trait MergeSplitProcessProvider extends ProcessProvider {
  type ProjectType <: MergeSplitProject


  def mergeAndSplitProc = new MergeSplitProcess()

  class MergeSplitProcess extends Proc{

    import MergeSplitProject._

    def name: String = "MergeAndSplitProcess"

    def apply(workbook: Workbook): Workbook = {

      val wrapper = new WorkbookWrapper(workbook)
      project.tasks foreach(t => {
        wrapper.doTask(t)
      })

      workbook
    }


  }

  class WorkbookWrapper(workbook : Workbook) {
    import MergeSplitProject._

    def doTask(task : MergeSplitTask){
      task match{
        case MergeSheetTask(sheetName, columns) => doMerge(sheetName,columns)
        case SplitSheetTask(sheetName,columns) => doSplit(sheetName,columns)
        case RenameSheet(sheetName,newName) => doRenameSheet(sheetName,newName)
        case DeleteSheet(sheetName) => doDeleteSheet(sheetName)
        case RenameColumnTask(ca,newName) => doRenameColumn(ca,newName)
        case ConvertColumnTask(ca,func) => doConvertColumn(ca,func)
        case DeleteColumn(ca) => doDeleteColumn(ca)
        case IgnoreColumn(ca,ignore) => doIgnoreColumn(ca,ignore)
        case CopyTo(from,to) => doCopyTo(from,to)
        case MergeSelectTask(sheetName,columns,fromSheetName,func) => {
          doMergeSelect(sheetName,columns,fromSheetName,func)
        }

      }
    }

    def doMerge(sheetName : String , columns : List[ColumnAddress]) {
      checkColumnsExist(columns)
      val rowSize = checkAllColumnIsSameSize(columns)
      workbook.getSheet(sheetName) match{
        case Some(sheet) => {
          if (sheet.rowSize != rowSize){
            throw new SQLGenException("Row sizes must be same!",workbook)
          }
          foreachColumn(columns)(c => {
            logger.log("Marge %s to sheet:%s".format(c,sheetName))
            sheet.deleteColumn(c.columnName)
            sheet.addColumn(c.columnName,c.cells.map(_.value))
          })
        }
        case None => {
          val sheet = new Sheet(sheetName)
          workbook.addSheet(sheet)
          foreachColumn(columns)(c => {
            logger.log("Marge %s to sheet:%s".format(c,sheetName))
            sheet.addColumn(c.columnName,c.cells.map(_.value))
          })
        }
      }
    }

    def doSplit(sheetName : String, columns : List[ColumnAddress]) {
      val sheet = workbook(sheetName)

      columns.foreach(ca => {
        val c = sheet.column(ca.columnName)
        val targetSheet = workbook(ca.sheetName)
        targetSheet.addColumn(c.columnName,c.cells.map(_.value))
      })

    }

    def doRenameSheet(sheetName : String, newName : String) {
      logger.log("Rename sheet from %s to %s".format(sheetName,newName))
      workbook(sheetName).name := newName
    }

    def doDeleteSheet(sheetName : String) {
      logger.log("Delete sheeet " + sheetName)
      workbook.deleteSheet(sheetName)
    }
    def doRenameColumn(ca : ColumnAddress,newName : String) {
      logger.log("Rename column@%s from %s to %s".format(ca.sheetName,ca.columnName,newName))
      workbook(ca.sheetName).header(ca.columnName).name := newName
    }
    def doConvertColumn(ca : ColumnAddress,func : String => String) {
      logger.log("Convert column:%s".format(ca))
      workbook(ca.sheetName).column(ca.columnName).cells.foreach(c => {
        c := func(c.value)
      })
    }

    def doDeleteColumn(ca : ColumnAddress) {
      logger.log("Delete column:%s".format(ca))
      workbook(ca.sheetName).deleteColumn(ca.columnName)
    }
    def doIgnoreColumn(ca : ColumnAddress,ignore : Boolean) {
      logger.log("Switch column:%s output to %s".format(ca,ignore))
      workbook(ca.sheetName).header(ca.columnName).output_? = !ignore
    }
    def doCopyTo( from : ColumnAddress,to : List[ColumnAddress]){
      logger.log("Copy column%s to columns:%s".format(from,to))
      val c = workbook(from.sheetName).column(from.columnName)
      val values = c.cells.map(_.value)
      to.foreach(ca => {
        workbook.getSheet(ca.sheetName) match{
          case Some(sheet) => {
            sheet.addColumn(ca.columnName,values)
          }
          case None => {
            val sheet = new Sheet(ca.sheetName)
            workbook.addSheet(sheet)
            sheet.addColumn(ca.columnName,values)
          }
        }
      })

    }

    def doMergeSelect(sheetName : String,
                      columns : List[(String,String)],
                      fromSheetName : String, func : (Row,Row) => Boolean) {

      val sheet = workbook(sheetName)
      val fromSheet = workbook(fromSheetName)

      val filteredRows = sheet.foreachRow(myRow => {
        fromSheet.findFirstRowWhere(yourRow => func(myRow,yourRow)) match{
          case Some(row) => {
            columns.map(c => c._2 -> row(c._1).value)
          }
          case None => columns.map(c => c._2 -> "")
        }
      })

      val toCols = filteredRows.flatten.groupBy(_._1).mapValues(v => v.map(_._2))

      toCols.foreach({
        case (columnName,values) => {
          sheet.deleteColumn(columnName)
          sheet.addColumn(columnName,values)
        }
      })

    }


    // Helpers

    def foreachColumn(_columns : List[ColumnAddress])(func : Column => Any) = {
      _columns.groupBy(_.sheetName).foreach({
        case (sheetName,columns) => {
          val sheet = workbook(sheetName)
          columns.foreach(ca => {
            val col = sheet.column(ca.columnName)
            func(col)
          })
        }
      })
    }

    def checkColumnsExist( columns : List[ColumnAddress]) = {
      columns.groupBy(_.sheetName).foreach({
        case (sheetName,columns) => {
          workbook.getSheet(sheetName) match{
            case Some(sheet) => {
              columns.foreach(c => {
                if(!sheet.existColumn(c.columnName)){
                  throw new HeaderNotFoundException(sheetName,c.columnName)
                }
              })
            }
            case None => {
              throw new SheetNotFoundException(sheetName)
            }
          }
        }
      })
    }
    def checkAllColumnIsSameSize(columns : List[ColumnAddress]) = {
      val rowSizes = columns.groupBy(_.sheetName).keys.map( sheetName => {
        workbook.getSheet(sheetName) match{
          case Some(s) => s.rowSize
          case None => 0
        }
      })
      if(rowSizes.size > 0){
        val s = rowSizes.head
        if(!rowSizes.forall(_ == s)){
          throw new SQLGenException("Row sizes must be same",workbook)
        }else{
          s
        }
      }else{
        0
      }

    }


  }


}
