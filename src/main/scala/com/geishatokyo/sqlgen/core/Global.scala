package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.core.impl.{DefaultActionRepository, DefaultMetadataRepository}

/**
  * Created by takezoux2 on 2017/06/11.
  */
object Global {

  var defaultActionRepository: ActionRepository = new DefaultActionRepository()
  var defaultMetadataRepository : MetadataRepository = new DefaultMetadataRepository()

}
