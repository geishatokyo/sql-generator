package com.geishatokyo.sqlgen.generator.sql

/**
  * Created by takezoux2 on 2017/07/06.
  */
sealed trait QueryType {

}

object QueryType {
  case object Insert extends QueryType{
    override def toString: String = "insert"
  }
  case object Replace extends QueryType {
    override def toString: String = "replace"
  }
  case object Delete extends QueryType {
    override def toString: String = "delete"
  }
}
