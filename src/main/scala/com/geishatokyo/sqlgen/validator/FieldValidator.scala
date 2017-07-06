package com.geishatokyo.sqlgen.validator

import com.geishatokyo.sqlgen.core.Sheet
import com.geishatokyo.sqlgen.meta.Metadata

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait FieldValidator {


  def isAllFieldsExists(sheet: Sheet, metadata: Metadata): Boolean

}


