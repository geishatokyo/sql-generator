package com.geishatokyo.sqlgen.generator.sql

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait ColumnKind {

  val String = "String"
  val Integer = "Integer"
  val Number = "Number"
  val Date = "Date"


}

object ColumnKind extends  ColumnKind
