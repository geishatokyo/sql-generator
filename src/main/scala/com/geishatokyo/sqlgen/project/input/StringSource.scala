package com.geishatokyo.sqlgen.project.input

import com.geishatokyo.sqlgen.project.flow.Input

/**
  * Created by takezoux2 on 2016/08/08.
  */
class StringSource(strings: List[String]) {

  def asCsv : Input = {
    new CSVInput(strings)
  }

}
