package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.Project

/**
  * Created by takezoux2 on 2017/07/05.
  */
class ProjectProc(p: Project) extends Proc{
  override def apply(c: Context): Context = {
    val wb = p.apply(c, c.workbook)
    c(Context.Workbook) = wb
    c
  }
}
