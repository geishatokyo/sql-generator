package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process._

/**
  * Created by takezoux2 on 2017/07/05.
  */
class ConsoleOutputProc(val dataKey: Key[MultiData[Any]]) extends OutputProc[Any] {


  override def output(data: MultiData[Any], c: Context): Unit = {
    data.datas.foreach(d => {
      println("Name: " + d.name)
      println(d.asString)
    })
  }



}
