package com.geishatokyo.sqlgen.project.flow

/**
  * Created by takezoux2 on 2016/08/10.
  */
trait DataProcessor {


  def process(inputDatas: List[InputData]) : List[InputData] = Nil
}

