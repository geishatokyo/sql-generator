package com.geishatokyo.sqlgen.process.converter

import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata}
import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MetadataImportProc(path: String, dataKey: String, metaLoader: MetaLoader) extends Proc {

  override def apply(c: Context): Context = {
    val meta = metaLoader.load(path)
    c(dataKey) = meta
    c
  }
}


class SetMetadataProc(dataKey: String, metadata: Metadata) extends Proc{
  override def apply(c: Context): Context = {
    c(dataKey) = metadata
    c
  }
}