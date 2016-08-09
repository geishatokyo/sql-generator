package com.geishatokyo.sqlgen

import com.geishatokyo.sqlgen.sheet.{Cell, Column, ColumnType, Workbook}

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
        if(c.header.columnType == ColumnType.Any) {
          guessTypeFromName(c.columnName) match {
            case Some (ct) => {
              c.header.columnType = ct
            }
            case None => {
              c.header.columnType = guessTypeByColumnValues(c)
            }
          }
        }
      })
    })
    workbook
  }

  def guessTypeFromName(columnName : String) : Option[ColumnType.Value] = {
    nameToType.find(r => r._1.findFirstIn(columnName).isDefined) match{
      case Some((_,columnType)) => Some(columnType)
      case None => None
    }
  }

  val IntLike = 1
  val LongLike = 1 << 1
  val StringLike = 1 << 2
  val DateLike = 1 << 3
  val DoubleLike = 1 << 4

  def guessTypeByColumnValues(c: Column) = {

    def like(cell: Cell) = {
      var r = 0

      cell.asLongOp.foreach(_ => r |= LongLike)
      cell.asDateOp.foreach(_ => r |= DateLike)
      cell.asDoubleOp.foreach(_ => r |= DoubleLike)
      if(cell.value == null || cell.value.isInstanceOf[String]){
        r |= StringLike
      }
      r
    }

    val typeProbables = c.cells.foldLeft(-1)((l,cell) => {
      l & like(cell)
    })

    def isLike(t: Int) = {
      (typeProbables & t) != 0
    }

    if(isLike(LongLike)){
      ColumnType.Integer
    }else if(isLike(DoubleLike)){
      ColumnType.Double
    }else if(isLike(DateLike)){
      ColumnType.Date
    }else if(isLike(StringLike)){
      ColumnType.String
    }else{
      ColumnType.Any
    }




  }

  addAction(guessId)
  addPostActions(typeGuess)

}
