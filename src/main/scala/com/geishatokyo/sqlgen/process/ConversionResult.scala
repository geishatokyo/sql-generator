package com.geishatokyo.sqlgen.process

/**
  * Created by takezoux2 on 2017/07/05.
  */

trait ConvertedData[+T]{
  def name: String
  def value: T

  def asString: String
  def asBytes: Array[Byte]
}

case class StringData(name: String, value: String) extends ConvertedData[String] {
  override def asString: String = value
  override def asBytes: Array[Byte] = value.getBytes("utf-8")
}


case class MultiData[+T](datas : ConvertedData[T]*)