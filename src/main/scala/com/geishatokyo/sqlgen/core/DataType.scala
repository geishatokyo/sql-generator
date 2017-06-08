package com.geishatokyo.sqlgen.core

/**
  * Created by takezoux2 on 2017/05/27.
  */
sealed trait DataType {

}


object DataType{
  case object Integer extends DataType
  case object Number extends DataType
  case object String extends DataType
  case object Date extends DataType
  case object Boolean extends DataType
}