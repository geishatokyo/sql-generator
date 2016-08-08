package com.geishatokyo.sqlgen.project3

import com.geishatokyo.sqlgen.sheet.{ColumnType, Workbook}

import scala.util.matching.Regex

/**
 * Created by takezoux2 on 15/05/05.
 */
trait StandardGuess { self : Project =>

  def idNameCandidates: List[Regex] = List("^id$".r,".+Id$".r)

  def guessId(workbook: Workbook): Workbook = {
    workbook.sheets.foreach(sheet => {
      sheet.columns.find(s => {
        idNameCandidates.exists(_.findFirstIn(s.columnName).isDefined)
      }) match{
        case Some(id) => {
          sheet.replaceIds(id.columnName)
        }
        case None => {
          sheet.replaceIds(sheet.column(0).columnName)
        }
      }
    })
    workbook
  }


  def nameToType : List[(Regex,ColumnType.Value)] = List(
    ".+Date$".r -> ColumnType.Date,
    ".+Time$".r -> ColumnType.Date,
    ".+Name$".r -> ColumnType.String,
    "^name$".r -> ColumnType.String,
    "^id$".r -> ColumnType.Integer,
    ".+Id$".r -> ColumnType.Integer,
    "^thumbnail$".r -> ColumnType.String
  )

  def typeGuess(workbook: Workbook): Workbook = {
    workbook.sheets.foreach(sheet => {
      sheet.columns.foreach(c => {
        guessTypeFromName(c.columnName).foreach(ct => {
          c.header.columnType = ct
        })
      })
    })
    workbook
  }

  addAction(guessId)
  addAction(typeGuess)

  def guessTypeFromName(columnName : String) : Option[ColumnType.Value] = {
    nameToType.find(r => r._1.findFirstIn(columnName).isDefined) match{
      case Some((_,columnType)) => Some(columnType)
      case None => None
    }
  }

}
