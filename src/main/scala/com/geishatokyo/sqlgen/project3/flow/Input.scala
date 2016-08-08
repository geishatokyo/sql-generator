package com.geishatokyo.sqlgen.project3.flow

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project3.Project
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
