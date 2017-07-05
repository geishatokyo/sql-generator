package com.geishatokyo.sqlgen.project.input


import com.geishatokyo.sqlgen.project.flow.{InputData, Input}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/08.
  */
class WorkbookInput(wbs: Workbook*) extends Input {
  override def read(): List[InputData] = {
    wbs.map(wb => InputData(context,wb)).toList
  }
}
