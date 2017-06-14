package com.geishatokyo.sqlgen.loader

import com.geishatokyo.sqlgen.core.Metadata
import com.geishatokyo.sqlgen.core.metadata.TSConfMetadata
import com.typesafe.config.Config

/**
  * Created by takezoux2 on 2017/06/11.
  */
class TypesafeConfigMetadataLoader(source: Source[Config]) extends MetadataLoader {


  def load(): Metadata = {
    new TSConfMetadata(source.load())
  }


}
