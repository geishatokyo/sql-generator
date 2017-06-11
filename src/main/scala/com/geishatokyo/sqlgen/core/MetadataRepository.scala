package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/06/10.
  */
trait MetadataRepository {

  private var metadatas: Map[String,Metadata] = Map.empty


  def apply(workbookName: String) : Metadata = {
    metadatas.getOrElse(workbookName, {
      new Metadata()
    })
  }

  def update(workbookName: String, metadata: Metadata) = synchronized{
    metadatas += workbookName -> metadata
  }

}

