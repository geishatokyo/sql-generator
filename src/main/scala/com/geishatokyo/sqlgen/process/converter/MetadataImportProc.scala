package com.geishatokyo.sqlgen.process.converter

import java.io.File

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata}
import com.geishatokyo.sqlgen.process.{Context, Key, Proc}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class MetadataImportProc(path: String, dataKey: Key[Metadata], metaLoader: MetaLoader) extends Proc {

  override def apply(c: Context): Context = {
    if(c.has(dataKey)) {
      Logger.log(s"Metadata:${dataKey} already imported")
      c
    }else {
      val f = new File(path)
      val meta = if(!f.exists() || !f.isFile ) {
        Logger.log(s"Metadata:${path} not exists")
        Metadata.Empty
      } else {
        metaLoader.load(path)
      }
      c(dataKey) = meta
      c
    }
  }
}


class SetMetadataProc(dataKey: Key[Metadata], metadata: Metadata) extends Proc{
  override def apply(c: Context): Context = {
    c(dataKey) = metadata
    c
  }
}