package com.geishatokyo.sqlgen.project.output

import com.geishatokyo.sqlgen.project.flow.{InputData, Output}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:45
 */
class ConsoleOutput extends Output{


  override def output(inputDatas: List[InputData]): Unit = {
    inputDatas.foreach(id => {
      println(id.workbook)
    })
  }
}
