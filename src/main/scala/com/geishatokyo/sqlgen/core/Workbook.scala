package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.meta.Metadata
import com.geishatokyo.sqlgen.setting.{DefaultWorkbookConfig, WorkbookConfSupport, WorkbookConfiguration}

import scala.collection.mutable
import scala.util.matching.Regex

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Workbook(var name: String,
               val config: WorkbookConfiguration = DefaultWorkbookConfig) extends WorkbookConfSupport {

  private var _sheets: List[Sheet] = Nil
  def sheets = _sheets

  private var _metadatas: Map[String,Metadata] = Map.empty
  def metadatas = _metadatas


  private var _note = Map.empty[String,Any]
  def note = _note

  def address = s"Workbook:${name}"

  def apply(name: String): Sheet = {
    getSheet(name) getOrElse {
      throw SQLGenException.atWorkbook(this, s"Sheet:${name} not found")
    }
  }

  def getSheet(name: String): Option[Sheet] = {
    _sheets.find(s => eqStr(s.name,name))
  }
  def hasSheet(name: String) = {
    _sheets.exists(s => eqStr(s.name,name))
  }

  def sheetsMatchingTo(r: Regex) = {
    _sheets.find {
      s => r.findFirstIn(s.name).isDefined
    }
  }


  def addSheet(sheet: Sheet): Sheet = {
    if(sheet._parent != this && sheet._parent != null){
      throw SQLGenException.atWorkbook(this, s"Sheet:${sheet.name} is already added to another workbook")
    }
    _sheets = _sheets :+ sheet
    sheet._parent = this
    sheet
  }


  def addSheet(name: String):Sheet = {
    addSheet(new Sheet(this, name))
  }

  def removeSheet(name: String) = {
    getSheet(name) match{
      case Some(sheet) => {
        sheet._parent = null
        _sheets = _sheets.filter(_ != sheet)
        true
      }
      case None => {
        false
      }
    }
  }


  def contains(name: String) = {
    hasSheet(name)
  }

  def addMetadata(metadata: Metadata) = {
    if(metadatas.contains(metadata.name)) {
      throw SQLGenException.atWorkbook(this, s"Already contains metadata:${metadata.name}")
    }
    _metadatas += (metadata.name -> metadata)
    this
  }

  def addNote(key: String, value: Any) = {
    _note += (key -> value)
    this
  }

  def copy(): Workbook = {
    var wb = new Workbook(name,config)
    wb._metadatas = this.metadatas.mapValues(_.copy())
    this._sheets.map(_.copyTo(wb))
    wb._note = this._note
    wb

  }

}
