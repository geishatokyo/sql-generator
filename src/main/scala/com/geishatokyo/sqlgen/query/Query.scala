package com.geishatokyo.sqlgen.query

import scala.util.matching.Regex

/**
  * データアクセスのためのシンプルなクエリの実装
  *
  * ```
  * Query.from("User").where(
  *   Eq("gender","male") or
  *   (Eq("age", 20) and Range("age", 15, 20))
  * )
  * ```
  *
  * Created by takezoux2 on 2017/07/07.
  */
case class Query(from: From, where: Condition) {


  override def toString: String = {
    s"FROM ${from.sheetName} WEHRE ${where}"
  }
}

object Query {
  def from(sheetName: String) = From(sheetName)
}


case class From(sheetName: String) {

  def where(c: Condition) = Query(this,c)

  def whereEq(columnName: String, v: Any) = Query(this, Eq(columnName, v))

  def idOf(v: Any) = Query(this, Eq("id",v))

}



trait Condition {
  def &(c: Condition) = And(this,c)
  def |(c: Condition) = Or(this,c)
}

case class Eq(columnName: String, value: Any) extends Condition {
  override def toString: String = {
    s"(${columnName} = ${value})"
  }
}

/**
  *
  *
  * @param columnName
  * @param min inclusive
  * @param max inclusive
  */
case class Range(columnName: String, min: Double, max: Double) extends Condition {
  override def toString: String = {
    s"(${min} <= ${columnName} <= max)"
  }
}
case class RegexMatch(columnName: String, r: Regex) extends Condition {
  override def toString: String = {
    s"${columnName} regex ${r}"
  }
}

case class And(c1: Condition, c2: Condition) extends Condition {
  override def toString: String = {
    s"(${c1} & ${c2})"
  }
}
case class Or(c1: Condition, c2: Condition) extends Condition {
  override def toString: String = {
    s"(${c1} | ${c2})"
  }
}
