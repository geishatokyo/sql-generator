package com.geishatokyo.sqlgen.validator

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.{Column, Header, Sheet, Workbook}
import com.geishatokyo.sqlgen.meta.{ColumnMeta, ExportStrategy, Metadata, SheetMeta}

/**
  * Created by takezoux2 on 2018/03/03.
  */
trait MetadataValidator {

  def metadataName: String

  def applyMetadata(wb: Workbook): Workbook = {
    wb.metadatas.get(metadataName) match {
      case Some(metadata) => {
        val validated = _validate(wb, metadata)
        //validated.name = metadata.name
        validated
      }
      case None => wb
    }
  }

  private def _validate(wb: Workbook, wbMeta: Metadata) = {
    wbMeta.sheetNotFoundExportStrategy match {
      case ExportStrategy.ThrowException => {
        wbMeta.sheetMetas.foreach(sm => {
          if(!wb.hasSheet(sm.name)) {
            throw SQLGenException.atWorkbook(wb, s"Sheet:${sm.name} not found in Sheet")
          }
        })
      }
      case ExportStrategy.Export => {
        wbMeta.sheetMetas.foreach(sm => {
          if(!wb.hasSheet(sm.name)) {
            wb.addSheet(sm.name)
          }
        })
      }
      case ExportStrategy.DontExport => {

      }
    }

    wb.sheets.foreach(sheet => {

      if(sheet.isIgnore) wb.removeSheet(sheet.name)
      else {
        sheet.getMetadata(metadataName) match {
          case Some(metadata) => {
            if(metadata.isIgnore) wb.removeSheet(sheet.name)
            else {
              _validateSheet(sheet, metadata)
            }
          }
          case None => {
            wbMeta.noMetaSheetExportStrategy match {
              case ExportStrategy.Export => {

              }
              case ExportStrategy.DontExport => {
                wb.removeSheet(sheet.name)
              }
              case ExportStrategy.ThrowException => {
                throw SQLGenException.atSheet(sheet,s"need metadata")
              }
            }
          }
        }
      }
    })
    wb

  }

  private def _validateSheet(sheet: Sheet, sheetMeta: SheetMeta) = {

    sheetMeta.getColumnNotFoundExportStrategy() match {
      case ExportStrategy.DontExport =>
      case ExportStrategy.Export => {
        sheetMeta.columnMetas.foreach(cm => {
          if(!sheet.hasColumn(cm.name)) {
            sheet.addHeader(cm.name)
          }
        })
      }
      case ExportStrategy.ThrowException => {
        sheetMeta.columnMetas.foreach(cm => {
          if(!sheet.hasColumn(cm.name)) {
            throw SQLGenException.atSheet(sheet, s"Column:${cm.name} not found in Sheet")
          }
        })
      }
    }


    val removes = sheet.headers.filter(header => {
      if(header.isIgnore) {
        true
      } else {
        sheetMeta.getColumnMeta(header.name) match {
          case Some(meta) => {
            _validateColmns(header, meta)
            meta.isIgnore
          }
          case None => {
            sheetMeta.getNoMetaColumnExportStrategy() match {
              case ExportStrategy.Export => false
              case ExportStrategy.DontExport => true
              case ExportStrategy.ThrowException => {
                throw SQLGenException.atHeader(header, "need metadata")
              }
            }
          }
        }
      }
    })

    sheet.removeColumns(removes.map(_.name):_*)
    sheet.headers.foreach(h => {
      if(sheetMeta.primaryIndex.contains(h.name)){
        h.isId = true
      }
    })
    sheet.name = sheetMeta.className

  }

  private def _validateColmns(header: Header, columnMeta: ColumnMeta) = {
    header.name = columnMeta.name
    header.columnType = Some(columnMeta.className)
    header.isIgnore = columnMeta.isIgnore
    header.isId = false
  }




}
