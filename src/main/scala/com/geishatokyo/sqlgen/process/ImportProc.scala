package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait ImportProc extends Proc{

  def importKey: Key[List[Workbook]] = Context.Import

  def load(c: Context): Workbook

  override def apply(c: Context): Context = {
    val wb = load(c)
    if(c.has(importKey)) {
      val workbooks = c(importKey)
      c(importKey) = wb :: workbooks
    } else {
      c(importKey) = wb :: Nil
    }
    c
  }
}
