package com.geishatokyo.sqlgen.project.flow

import com.geishatokyo.sqlgen.{Project, Context}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/05.
  */
trait Input {

  protected var context = new Context()

  def modifyContext(func: Context => Context) = {
    context = func(context)
    this
  }

  def withContext(c: Context) = {
    this.context = c
    this
  }


  def >>(proc: DataProcessor) = {
    Executor(this,List(proc))
  }

  def read() : List[InputData]

}

case class InputData(context: Context, workbook: Workbook)
