package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait InputProc extends Proc {

  def load(c: Context) : Option[Workbook]
  def mergeWithExistingWorkbook = true
  def workingDir: Option[String]

  override def apply(c: Context): Context = {
    load(c) match {
      case Some(workbook) => {
        if(c.hasWorkbook) {
          if(mergeWithExistingWorkbook) {
            WorkbookMerger.merge(c.workbook, workbook)
          } else {
            throw SQLGenException("Workbook already exists")
          }
        } else {
          c(Context.Workbook) = workbook
        }
      }
      case None => {
        throw SQLGenException("No files are loaded.Please check xls,xlsx,csv file exists in directory")
      }
    }

    workingDir match{
      case Some(workingDir) => {
        c(Context.WorkingDir) = workingDir
      }
      case None =>
    }

    c

  }
}
