package com.geishatokyo.sqlgen.project3.input

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project3.flow.Input
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/08.
  */
class WorkbookInput(wb: Workbook) extends Input {
  override def read(): (Context,Workbook) = {
    (new Context(),wb)
  }
}
