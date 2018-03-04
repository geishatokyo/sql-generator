package com.geishatokyo.sqlgen.process.mete

import com.geishatokyo.sqlgen.meta.Metadata
import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2018/03/04.
  */
class MetaSetProc(metas: Metadata*) extends Proc {
  override def apply(c: Context): Context = {
    metas.foreach(m => {
      c.workbook.addMetadata(m)
    })
    c
  }
}
