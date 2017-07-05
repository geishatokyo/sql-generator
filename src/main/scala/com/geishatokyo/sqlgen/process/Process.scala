package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/06/30.
  */

trait Proc  {

  def apply(c: Context): Context


  def >>(proc: Proc): Proc = {
    ProcNode(this,proc)
  }

  def execute(c: Context): Context = {
    apply(c)
  }


}

case class ProcNode(beforeProc: Proc, currentProc: Proc) extends Proc {

  override def apply(c: Context): Context = {
    currentProc.apply(c)
  }

  override def execute(c: Context): Context = {
    val c2 = beforeProc.execute(c)
    currentProc.apply(c2)
  }


}