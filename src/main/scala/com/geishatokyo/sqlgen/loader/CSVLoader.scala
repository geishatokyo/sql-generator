package com.geishatokyo.sqlgen.loader

import java.io.{BufferedInputStream, DataInputStream, FileInputStream, StringReader}

import com.geishatokyo.sqlgen.core.Workbook

import scala.collection.mutable.ListBuffer
import com.github.tototoshi.csv

/**
  * Created by takezoux2 on 2017/06/11.
  */
class CSVLoader(filePath: String, sep: Char = ',') extends WorkbookLoader {

  def load() = {
    val reader = csv.CSVReader.open(filePath, "utf-8")
    val values = reader.all()
    reader.close()
    val workbook = new Workbook("hoge")
    addSheets(workbook, values)
  }

  def addSheets(wb: Workbook, values: List[List[String]]) = {
    var left = values
    var sheet = wb.addSheet("Sheet1")
    while(left.size > 0) {
      val line = left.head
      left = left.tail

      if(line.size > 0) {
        val head = line.head
        if(head.trim.startsWith("#")) {
          val comment = head.substring(head.indexOf("#") + 1)
          // # @Seet SheetName
          // の形式のコメントが有った場合、シート分割する
          if(comment.indexOf("@Sheet") >= 0){
            val sheetName = comment.substring(comment.indexOf("@Sheet") + "@Sheet".length).split(" ").head
            if(sheet.name != "Sheet1") {
              sheet = wb.addSheet("")
            } else if(wb.contains(sheetName)) {
              sheet = wb(sheetName)
            } else {
              sheet = wb.addSheet(sheetName)
            }
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
