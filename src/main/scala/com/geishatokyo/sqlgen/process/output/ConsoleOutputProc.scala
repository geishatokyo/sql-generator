package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process.{Context, MultiData, OutputProc, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class ConsoleOutputProc(val dataKey: String) extends OutputProc[Any] {


  override def output(data: MultiData[Any], c: Context): Unit = {
    data.datas.foreach(d => {
      println("Name: " + d.name)
      println(d.asString)
    })
  }



}
