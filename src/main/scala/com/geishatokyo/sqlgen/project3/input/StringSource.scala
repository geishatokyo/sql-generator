package com.geishatokyo.sqlgen.project3.input

import com.geishatokyo.sqlgen.project3.flow.Input

/**
  * Created by takezoux2 on 2016/08/08.
  */
class StringSource(strings: List[String]) {

  def asCsv : Input = {
    new CSVInput(strings)
  }

}
