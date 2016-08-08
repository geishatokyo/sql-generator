package com.geishatokyo.sqlgen.project3.input

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project3.flow.Input
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}

/**
  * Created by takezoux2 on 2016/08/08.
  */
class CSVInput(csvs: List[String],seps: Array[Char] = ",".toCharArray) extends Input {
  override def read(): (Context,Workbook) = {
    val workbook = new Workbook()
    csvs.zipWithIndex.foreach({
      case (s,index) => {
        val sheet = loadCsv(index,s)
        workbook.addSheet(sheet)
      }
    })
    (new Context,workbook)
  }



  def loadCsv(index: Int, csv: String) = {
    val sheet = new Sheet("Sheet" + index)

    val lines = csv.lines

    var setHeader = false

    lines.foreach(line => {
      if(line.startsWith("#")){
        val command = line.drop(1).trim
        if(command.startsWith("Name:")){
          sheet.name = command.drop("Name:".length).trim
        }
      }else{
        val splited = split(line)

        if(setHeader){
          sheet.addRow(splited.toList)
        }else{
          sheet.addColumns(splited :_*)
          setHeader = true
        }
      }
    })
    sheet


  }

  def split(line: String) = {
    line.split(seps)
  }

}
