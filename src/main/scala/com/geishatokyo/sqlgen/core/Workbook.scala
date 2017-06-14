package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException

import scala.util.matching.Regex

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Workbook(val name: String, metadataRepoOp: Option[MetadataRepository] = None, actionRepoOp: Option[ActionRepository] = None) {

  private var _sheets: Map[String,Sheet] = Map.empty
  def sheets = _sheets.values

  def metadataRepository = metadataRepoOp match {
    case Some(repo) => repo
    case None => Global.defaultMetadataRepository
  }

  def metadata: Metadata = {
    metadataRepository(name)
  }

  def actionRepository = actionRepoOp match {
    case Some(repo) => repo
    case None => Global.defaultActionRepository
  }


  def apply(name: String): Sheet = {
    _sheets.getOrElse(name, {
      throw new SQLGenException(s"Sheet:${name} not found in Workbook:${name}")
    })
  }

  def getSheet(name: String): Option[Sheet] = {
    _sheets.get(name)
  }

  def sheetsMatchingTo(r: Regex) = {
    _sheets.find {
      case (k,v) => r.findFirstIn(k).isDefined
    } map(_._2)
  }


  def addSheet(sheet: Sheet): Sheet = {
    if(sheet._parent != null){
      throw new SQLGenException(s"Sheet:${sheet.name} is already added to another workbook")
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
