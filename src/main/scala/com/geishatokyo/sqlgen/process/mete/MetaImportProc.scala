package com.geishatokyo.sqlgen.process.mete

import com.geishatokyo.sqlgen.meta.MetaLoader
import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2018/03/04.
  */
class MetaImportProc(loader: MetaLoader, files: String*) extends Proc{

  override def apply(c: Context): Context = {
    val metas = files.map(loader.load(_))

    metas.foreach(m => {
      c.workbook.addMetadata(m)
    })

    c
  }
}
