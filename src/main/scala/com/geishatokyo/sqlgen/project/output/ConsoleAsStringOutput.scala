package com.geishatokyo.sqlgen.project.output

import com.geishatokyo.sqlgen.project.flow.{InputData, Output}

/**
  * Created by takezoux2 on 2016/08/16.
  */
class ConsoleAsStringOutput(conv: InputData => List[String]) extends Output {
  override def output(inputDatas: List[InputData]): Unit = {

    for(id <- inputDatas;
    s <- conv(id)) println(s)

  }
}
