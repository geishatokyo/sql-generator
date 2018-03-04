package com.geishatokyo.sqlgen.process.converter

import java.io.File

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata}
import com.geishatokyo.sqlgen.process.{Context, Key, Proc}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MetadataImportProc(path: String, metaLoader: MetaLoader) extends Proc {

  override def apply(c: Context): Context = {
    val metadata = metaLoader.load(path)
    c.workbook.addMetadata(metadata)
    c
  }
}
