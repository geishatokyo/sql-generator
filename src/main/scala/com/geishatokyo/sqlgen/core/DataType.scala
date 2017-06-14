package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/27.
  */
sealed abstract class DataType(val name: String) {

  def synonyms: Set[String] = Set.empty

  def isThisType(s: String) = {
    name == s ||
    synonyms.contains(s)
  }

}


object DataType{
  case object Integer extends DataType("Integer"){
    override def synonyms: Set[String] = Set("Long","Int","Short","Byte")
  }
  case object Number extends DataType("Number") {
    override def synonyms: Set[String] = Set("Double","Float")
  }
  case object String extends DataType("String") {
    override def synonyms: Set[String] = Set("VarChar","Text","Char","Character")
  }
  case object Date extends DataType("Date") {
    override def synonyms: Set[String] = Set("Time","DateTime")
  }
  case class AnyType(_name: String, override val synonyms: Set[String]) extends DataType(_name)


  def fromString(s: String): DataType = {
    if(Integer.isThisType(s)) Integer
    else if(Number.isThisType(s)) Number
    else if(String.isThisType(s)) String
    else if(Date.isThisType(s)) Date
    else AnyType(s,Set.empty)
  }

}