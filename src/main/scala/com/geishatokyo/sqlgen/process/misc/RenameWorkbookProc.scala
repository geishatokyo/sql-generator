package com.geishatokyo.sqlgen.process.misc

import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2017/07/07.
  */
class RenameWorkbookProc(workbookName: String) extends Proc{
  override def apply(c: Context): Context = {
    c.workbook.name = workbookName
    c
  }
}
