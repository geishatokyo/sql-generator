package com.geishatokyo.sqlgen.loader

import java.io._

import com.geishatokyo.sqlgen.core.Workbook

import scala.collection.mutable.ListBuffer
import com.github.tototoshi.csv

/**
  * Created by takezoux2 on 2017/06/11.
  */
class CSVLoader(source: Source[Iterable[String]], sep: Char = ',') extends WorkbookLoader {

  def load() = {

    val workbook = new Workbook(source.name)
    source.load().foreach(csvStr => {
      val reader = csv.CSVReader.open(new StringReader(csvStr))
      val values = reader.all()
      reader.close()
      addSheets(workbook, values)
    })
    workbook
  }

  def addSheets(wb: Workbook, values: List[List[String]]) = {
    var left = values
    var sheetName = source.name
    def sheet = {
      if(wb.contains(sheetName)) {
        wb(sheetName)
      } else {
        wb.addSheet(sheetName)
      }
    }


    while(left.size > 0) {
      val line = left.head
      left = left.tail

      if(line.size > 0 && !(line.size == 1 && line(0).trim.length == 0)) {
        val head = line.head
        if(head.trim.startsWith("#")) {
          val comment = head.substring(head.indexOf("#") + 1)
          // # @Seet SheetName
          // の形式のコメントが有った場合、シート分割する
          if(comment.indexOf("@Sheet") >= 0){
            sheetName = comment.substring(comment.indexOf("@Sheet") + "@Sheet".length).split(" ").find(_.length > 0).getOrElse("Unknown")
          }
        } else {
          if(sheet.columns.length == 0) {
            sheet.addHeaders(line:_*)
          } else {
            sheet.addRow(line:_*)
          }
        }
      }
    }

    wb
  }

}
