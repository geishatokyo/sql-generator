package com.geishatokyo.sqlgen.core
import java.time.ZonedDateTime
import java.util.Date

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.operation.{Operator, Variable, VariableConverter}

import scala.util.Try

/**
  * Created by takezoux2 on 2017/05/26.
  */
class Cell( _parent: Sheet,
            private[core] var _rowIndex: Int,
            private[core] var _columnIndex: Int){

  def parent: Sheet = _parent
  def columnIndex = _columnIndex
  def rowIndex = _rowIndex

  def row = _parent.rows(_rowIndex)
  def column = _parent._columns(_columnIndex)
  def header = _parent.header(columnIndex)


  private[core] def variableConverter: VariableConverter = {
    parent.parent.actionRepository.getVaribaleConverter(this)
  }

  private[core] var variable: Variable = null

  def value = if(variable == null) {
    null
  } else {
    variable.raw
  }
  def value_=(v: Any) = {
    if(variable == null || v != variable || v != variable.raw){
      variable = variableConverter.toVariable(v)
    }
  }

  def :=(v: Any) = this.value = v

  def +(v: Any) = {
    variableConverter.getApplier(Operator.Add)(variable, variableConverter.toVariable(v))(this)
  }
  def -(v: Any) = {
    variableConverter.getApplier(Operator.Sub)(variable, variableConverter.toVariable(v))(this)
  }
  def *(v: Any) = {
    variableConverter.getApplier(Operator.Mod)(variable, variableConverter.toVariable(v))(this)
  }
  def /(v: Any) = {
    variableConverter.getApplier(Operator.Div)(variable, variableConverter.toVariable(v))(this)
  }
  def %(v: Any) = {
    variableConverter.getApplier(Operator.Mod)(variable, variableConverter.toVariable(v))(this)
  }

  /**
    *
    * @return
    */
  def asLong: Long = variable.asDouble.toLong

  /**
    *
    * @return
    */
  def asString: String = variable.asString

  /**
    *
    * @return
    */
  def asDouble: Double = variable.asDouble

  /**
    *
    * @return
    */
  def asJavaTime: ZonedDateTime = variable.asDate

  /**
    *
    * @return
    */
  def asBool: Boolean = variable.asDouble > 0

  def rawValue: Any = variable.raw

  private def throwE(message: String) = {
    val m = s"${this.parent.parent.name}/${this.parent.name} Row:${this.rowIndex} Column:${this.header.name} -- ${message}"
    throw new SQLGenException(m)
  }

}

object Cell{

  def apply(_parent: Sheet, row: Int, column: Int, value: Any) : Cell = {
    val cell = new Cell(_parent, row, column)
    cell.value = value
    cell
  }

}
