package com.geishatokyo.sqlgen.core.impl

import com.geishatokyo.sqlgen.core.DataType

/**
  * Created by takezoux2 on 2017/06/14.
  */
class ColumnTypeGuesser {

  def fromName(name: String): DataType = {
    if(longable.check(name)) DataType.Integer
    else if(timeable.check(name)) DataType.Date
    else DefaultType
  }

  var DefaultType = DataType.String

  var longable = GuessSet(
    List(
      "total"
    ),
    List(
      "id",
      "age",
      "count",
      "counter",
      "num",
      "rarity",
      "level",
      "type",
      "cost",
      "status",
      "value",
      "price",
      "exp",
      "hour",
      "minute",
      "sec",
      "second",
      "gauge"
    ),
    Nil
  )
  var timeable = GuessSet(
    List(
      "time"
    ),
    List(
      "time",
      "date",
      "datetime"
    ),
    Nil
  )
  var doublable = GuessSet(
    Nil,
    List(
      "ratio",
      "rate"
    ),
    Nil
  )

}

object ColumnTypeGuesser extends ColumnTypeGuesser

case class GuessSet(prefix: List[String],suffix: List[String], contain: List[String])
{

  def check(name: String) = {
    prefix.exists(p => {
      name == p ||
      name.startsWith(p + "_") ||
      name.startsWith(p.capitalize)
    }) ||
    suffix.exists(s => {
      name == s||
      name.endsWith("_" + s) ||
      name.endsWith(s.capitalize)
    }) ||
    contain.exists(c => {
      name.contains(c) ||
      name.contains(c.capitalize)
    })
  }


}
