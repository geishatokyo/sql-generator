package com.geishatokyo.sqlgen.process.converter

import com.geishatokyo.sqlgen.meta.{MetaLoader, Metadata, TypeSafeConfigMetaLoader}
import com.geishatokyo.sqlgen.process.{Context, Key, Proc}

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait UsingMetaFile { self: Proc =>

  def defaultMetaFilePath: String
  def metadataKey : Key[Metadata]
  def metaLoader: MetaLoader = new TypeSafeConfigMetaLoader()

  private var metaProc : Proc = {
    new MetadataImportProc(defaultMetaFilePath, metadataKey, metaLoader)
  }
  override def thisProc: Proc = {
    metaProc >> this
  }

  def withMeta(meta: Metadata): this.type = {
    this.metaProc = new SetMetadataProc(metadataKey, meta)
    this
  }
  def withMetaFile(path: String): this.type = {
    this.metaProc = new MetadataImportProc(path, metadataKey, metaLoader)
    this
  }

  def getMetadata(c: Context): Metadata = {
    if(c.has(metadataKey)) {
      c(metadataKey)
    } else {
      Metadata.Empty
    }
  }

}
