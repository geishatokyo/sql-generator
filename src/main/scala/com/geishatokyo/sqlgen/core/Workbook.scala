package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.SQLGenException

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Workbook(val name: String, metadataRepoOp: Option[MetadataRepository] = None, actionRepoOp: Option[ActionRepository] = None) {

  private var sheets: Map[String,Sheet] = Map.empty

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




  def addSheet(sheet: Sheet): Sheet = {
    if(sheet._parent != null){
      throw new SQLGenException(s"Sheet:${sheet.name} is already added to another workbook")
    }
    sheets = sheets + (sheet.name -> sheet)
    sheet._parent = this
    sheet
  }

  def addSheet(name: String):Sheet = {
    addSheet(new Sheet(name))
  }

  def removeSheet(name: String) = {
    sheets.get(name) match{
      case Some(sheet) => {
        sheet._parent = null
        sheets = sheets - name
        true
      }
      case None => {
        false
      }
    }
  }


}
