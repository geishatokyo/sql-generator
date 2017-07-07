package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process.{Context, ImportProc, InputProc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class WorkbookImportProc(workbook: Workbook) extends ImportProc {
  override def load(c: Context): Workbook = {
    workbook
  }

}
