package com.geishatokyo.sqlgen.project.flow

import com.geishatokyo.sqlgen.{Project, Context}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/05.
  */
trait Input {

  def >>(project: Project) = {
    Executor(this,project)
  }

  def read() : (Context,Workbook)

}
