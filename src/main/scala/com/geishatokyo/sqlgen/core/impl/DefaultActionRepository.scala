package com.geishatokyo.sqlgen.core.impl

import com.geishatokyo.sqlgen.core.conversion.{DateConversion, UnixTimeBaseConversion, VariousStringFormatConversion}
import com.geishatokyo.sqlgen.core.operation.VariableConverter
import com.geishatokyo.sqlgen.core.{ActionRepository, Cell, Column, ColumnInfoAccessor}

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

  override def getColumnInfoAccessor(column: Column): ColumnInfoAccessor = {
    new DefaultColumnInfoAccessor(column)
  }
}


class DefaultDateConversion extends UnixTimeBaseConversion with VariousStringFormatConversion {

}

class DefaultColumnInfoAccessor(column: Column) extends ColumnInfoAccessor {
  override def isId: Boolean = {
    column.header.name == "id"
  }

  override def columnType: String = {
    column.metadata.columnType.map(_.toString).getOrElse("String")
  }

  override def isIgnore: Boolean = {
    column.metadata.ignore == Some(true)
  }

  override def isUnique: Boolean = {
    column.metadata.unique == Some(true)
  }

  override def defaultValue: Any = {
    column.metadata.defaultValue.map(_.toString).getOrElse("")
  }
}