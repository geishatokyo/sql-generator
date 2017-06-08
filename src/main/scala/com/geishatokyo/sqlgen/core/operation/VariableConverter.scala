package com.geishatokyo.sqlgen.core.operation

/**
  * Created by takezoux2 on 2017/06/10.
  */
trait VariableConverter {


  def toVariable(v: Any): Variable


  def getApplier(operator: Operator): Applier


}


