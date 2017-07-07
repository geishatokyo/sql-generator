package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException

import scala.collection.mutable
import scala.util.matching.Regex

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Workbook(var name: String) {

  private var _sheets: Map[String,Sheet] = Map.empty
  def sheets = _sheets.values


  val note = mutable.Map.empty[String,Any]




  def apply(name: String): Sheet = {
    _sheets.getOrElse(name, {
      throw SQLGenException.atWorkbook(this, s"Sheet:${name} not found")
    })
  }

  def getSheet(name: String): Option[Sheet] = {
    _sheets.get(name)
  }
  def hasSheet(name: String) = {
    _sheets.contains(name)
  }

  def sheetsMatchingTo(r: Regex) = {
    _sheets.find {
      case (k,v) => r.findFirstIn(k).isDefined
    } map(_._2)
  }


  def addSheet(sheet: Sheet): Sheet = {
    if(sheet._parent != null){
      throw SQLGenException.atWorkbook(this, s"Sheet:${sheet.name} is already added to another workbook")
    }
    _sheets = _sheets + (sheet.name -> sheet)
    sheet._parent = this
    sheet
  }


  def addSheet(name: String):Sheet = {
    addSheet(new Sheet(name))
  }

  def removeSheet(name: String) = {
    _sheets.get(name) match{
      case Some(sheet) => {
        sheet._parent = null
        _sheets = _sheets - name
        true
      }
      case None => {
        false
      }
    }
  }


  def contains(name: String) = {
    _sheets.contains(name)
  }


  private[core] def changeSheetName(sheet: Sheet, newName: String) = {
    _sheets -= sheet.name
    _sheets += (newName -> sheet)
  }

}
