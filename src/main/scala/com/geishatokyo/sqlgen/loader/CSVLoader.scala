package com.geishatokyo.sqlgen.loader

import java.io._

import com.geishatokyo.sqlgen.core.Workbook

import scala.collection.mutable.ListBuffer
import com.github.tototoshi.csv

/**
  * Created by takezoux2 on 2017/06/11.
  */
class CSVLoader extends Loader {


  override def load(name: String, input: InputStream): Workbook = {
    val workbook = new Workbook(name)
    val reader = csv.CSVReader.open(new InputStreamReader(input,"utf-8"))
    val values = reader.iterator
    addSheets(workbook, values)

    reader.close()

    workbook
  }


  def addSheets(wb: Workbook, values: Iterator[Seq[String]]) = {
    var left = values

    var sheetName = wb.name
    def sheet = {
      if (wb.contains(sheetName)) {
        wb(sheetName)
      } else {
        wb.addSheet(sheetName)
      }
    }


    while(left.hasNext) {
      val line = left.next()

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
