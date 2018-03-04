package com.geishatokyo.sqlgen.meta

import java.util.Comparator

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.setting.CaseSensitiveStringComp

/**
  * Created by takezoux2 on 2017/07/05.
  */
case class Metadata(name: String, sheetMetas: List[SheetMeta]) {

  sheetMetas.foreach(_.parent = this)

  var stringComparator: Comparator[String] = CaseSensitiveStringComp

  // デフォルトの挙動は、
  // * Metadataに存在しないシート、カラムは出力されない
  // * Metadataに登録はあるが、シートが無い場合は無視される
  // * Metadataに登録はあるのに、カラムが存在しない場合は例外が発生する

  /**
    * Metadataの無いシートが存在した場合の挙動
    */
  var noMetaSheetExportStrategy: ExportStrategy = ExportStrategy.DontExport
  /**
    * Metadataの無いカラムが存在した時の挙動
    * SheetMetadataによって上書きされる
    */
  var noMetaColumnExportStrategy: ExportStrategy = ExportStrategy.DontExport
  /**
    * Metadataに登録されていないシートが存在した場合の挙動
    */
  var sheetNotFoundExportStrategy: ExportStrategy = ExportStrategy.DontExport
  /**
    * Metadataに登録されていないカラムが存在した場合の挙動
    */
  var columnNotFoundExportStrategy: ExportStrategy = ExportStrategy.ThrowException


  def getSheetMeta(name: String) = {
    sheetMetas.find(sheet => stringComparator.compare(sheet.name, name) == 0)
  }

  def copy() = {
    val m = Metadata(name, sheetMetas.map(_.copy))

    m.stringComparator = stringComparator
    m
  }

}

object Metadata {
  val AutoClass = "#Auto"
}


case class SheetMeta(name: String,
                     columnMetas: List[ColumnMeta]) {

  private[meta] var parent: Metadata = null

  var className: String = name
  var primaryIndex : List[String] = List("id")
  var isIgnore = false

  var noMetaColumnExportStrategy: Option[ExportStrategy] = None
  var columnNotFoundExportStrategy: Option[ExportStrategy] = None


  def getNoMetaColumnExportStrategy() = {
    if(parent == null) {
      noMetaColumnExportStrategy.getOrElse {
        throw SQLGenException("Unknown export strategy")
      }
    } else {
      noMetaColumnExportStrategy getOrElse parent.noMetaColumnExportStrategy
    }
  }

  def getColumnNotFoundExportStrategy() = {
    if(parent == null) {
      columnNotFoundExportStrategy.getOrElse {
        throw SQLGenException("Unknown export strategy")
      }
    } else {
      columnNotFoundExportStrategy getOrElse parent.columnNotFoundExportStrategy
    }
  }

  def getColumnMeta(name: String) = {
    if(parent == null) {
      columnMetas.find(_.name == name)
    } else {
      columnMetas.find(c => {
        parent.stringComparator.compare(c.name, name) == 0
      })
    }
  }

  private[meta] def copy() = {
    val s = SheetMeta(name, columnMetas.map(_.copy))
    s.className = className
    s.primaryIndex = primaryIndex
    s
  }



}

case class ColumnMeta(name: String) {

  var className: String = Metadata.AutoClass
  var isIgnore: Boolean = false



  def copy() = {
    val c = ColumnMeta(name)
    c.className = this.className
    c.isIgnore = this.isIgnore
    c
  }

}
