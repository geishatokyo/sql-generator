package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:03
 */
class Mediator(input : Input, project : Project) {


  def >>(output : Output) : Mediator = {
    input.read().foreach(wb => {
      val result = project(wb._2)
      output.write(wb._1,result)
    })
    this
  }

  def execute : List[Workbook] = {
    input.read().map(wb => {
      project(wb._2)
    })
  }


}
