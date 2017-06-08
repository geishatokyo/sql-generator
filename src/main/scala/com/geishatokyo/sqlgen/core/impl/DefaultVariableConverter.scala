package com.geishatokyo.sqlgen.core.impl

import java.time._
import java.util.Date

import com.geishatokyo.sqlgen.core.Cell
import com.geishatokyo.sqlgen.core.conversion.DateConversion
import com.geishatokyo.sqlgen.core.operation._

/**
  * Created by takezoux2 on 2017/06/10.
  */
class DefaultVariableConverter(dateConversion: DateConversion) extends VariableConverter {

  override def toVariable(v: Any): Variable = {
    v match{
      case c: Cell => c.variable
      case v: Variable => v
      case n: Int => new DoubleVariable(n, dateConversion)
      case n: Long => new DoubleVariable(n, dateConversion)
      case n: Short => new DoubleVariable(n, dateConversion)
      case n: Byte => new DoubleVariable(n, dateConversion)
      case n: Double => new DoubleVariable(n, dateConversion)
      case n: String => new StringVariable(n, dateConversion)
      case n: Date => DateVariable(n, dateConversion)
      case n: Boolean => new DoubleVariable(1, dateConversion)
      case n: ZonedDateTime => new DateVariable(n, dateConversion)
      case n => new AnyVariable(n, dateConversion)
    }
  }

  override def getApplier(operator: Operator): Applier = operator match{
    case Operator.Add => AddApplier
    case Operator.Sub => SubApplier
    case Operator.Mul => MulApplier
    case Operator.Div => DivApplier
    case Operator.Mod => ModApplier
  }
}
