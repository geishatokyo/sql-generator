package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.core.conversion.DateConversion
import com.geishatokyo.sqlgen.core.operation.VariableConverter

/**
  * Created by takezoux2 on 2017/06/10.
  */
trait ActionRepository {

  def getDateConversion: DateConversion
  def getVaribaleConverter(c: Cell): VariableConverter

}
