package com.geishatokyo.sqlgen.project.flow

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/05.
  */
trait Output{


  def output(inputDatas: List[InputData]) : Unit



}
