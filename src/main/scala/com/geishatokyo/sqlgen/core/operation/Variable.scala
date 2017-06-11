package com.geishatokyo.sqlgen.core.operation

import java.time.ZonedDateTime

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Cell

/**
  * Created by takezoux2 on 2017/06/08.
  */
trait Variable {

  def asDouble: Double
  def asString: String
  def asDate: ZonedDateTime
  def raw: Any

  def isEmpty : Boolean
}


object Variable{

  def throwNotSupportedOperator(c: Cell,op: Operator) = {
    val from = if(c.value == null) "null"
    else c.value.getClass.getName
    val address = s"Sheet:${c.parent.name} Row:${c.rowIndex} Col:${c.column.header.name}"
    throw new SQLGenException(s"${address} - ${from} doesn't support operation ${op}")
  }

  def throwNotSupportedOperator(c: Cell,op: Operator, v: Any) = {
    val from = if(c.value == null) "null"
    else c.value.getClass.getName
    val to = if(v == null) "null"
    else v.getClass.getName
    val address = s"Sheet:${c.parent.name} Row:${c.rowIndex} Col:${c.column.header.name}"
    throw new SQLGenException(s"${address} - ${from} doesn't support operation ${op} with ${to}")
  }

}