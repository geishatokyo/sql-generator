package com.geishatokyo.sqlgen.project.input

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project.flow.{InputData, Input}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}

/**
  * Created by takezoux2 on 2016/08/08.
  */
class CSVInput(csvs: List[String]) extends Input {
  override def read(): List[InputData] = {
    val workbook = new Workbook()
    csvs.zipWithIndex.foreach({
      case (s,index) => {
        val sheet = CSVLoader.load("Sheet" + index,s)
        workbook.addSheet(sheet)
      }
    })
    List(InputData(context.copy(),workbook))
  }

}
