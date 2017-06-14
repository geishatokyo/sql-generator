package com.geishatokyo.sqlgen.loader

import com.geishatokyo.sqlgen.core.Metadata

/**
  * Created by takezoux2 on 2017/06/11.
  */
trait MetadataLoader {

  def load(): Metadata
}
