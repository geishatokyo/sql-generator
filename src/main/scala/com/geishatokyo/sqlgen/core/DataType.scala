package com.geishatokyo.sqlgen.core

import com.amazonaws.auth.policy.conditions.DateCondition.DateComparisonType

/**
  * Created by takezoux2 on 2017/05/27.
  */
sealed abstract class DataType(val name: String) {


  override def toString: String = name

  override def equals(obj: scala.Any): Boolean = {
    obj match{
      case that : DataType => that.name == name
      case _ => false
    }
  }

  override def hashCode(): Int = name.hashCode
}



object DataType{
  case object Integer extends DataType("Integer"){
  }
  case object Number extends DataType("Number") {
  }
  case object String extends DataType("String") {
  }
  case object Date extends DataType("Date") {
  }
  case object Bool extends DataType("Bool") {
  }

  case object Duration extends DataType("Duration") {

  }

  case object Null extends DataType("Null") {

  }
}