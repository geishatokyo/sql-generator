package com.geishatokyo.sqlgen.core.impl

import com.geishatokyo.sqlgen.core.conversion.{DateConversion, UnixTimeBaseConversion, VariousStringFormatConversion}
import com.geishatokyo.sqlgen.core.operation.VariableConverter
import com.geishatokyo.sqlgen.core.{ActionRepository, Cell}

/**
  * Created by takezoux2 on 2017/06/11.
  */
class DefaultActionRepository extends ActionRepository{

  override val getDateConversion: DateConversion = {
    new DefaultDateConversion()
  }

  override def getVaribaleConverter(c: Cell): VariableConverter = {
    new DefaultVariableConverter(getDateConversion)
  }
}


class DefaultDateConversion extends UnixTimeBaseConversion with VariousStringFormatConversion {

}