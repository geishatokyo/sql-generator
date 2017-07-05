package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process.{Context, InputProc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class WorkbookInput(workbook: Workbook) extends InputProc {
  override def load(c: Context): Workbook = {
    workbook
  }

  override def workingDir: Option[String] = None
}
