package com.geishatokyo.sqlgen.core.operation

import com.geishatokyo.sqlgen.core.Cell

import scala.util.Try

/**
  * Created by takezoux2 on 2017/06/08.
  */
trait Applier {
  def apply(a: Variable, b: Variable)(implicit c: Cell): Any
}

object AddApplier extends Applier  {
  override def apply(a: Variable, b: Variable)(implicit c: Cell): Any = {
    Try { a.asDouble + b.asDouble} orElse
    Try { a.asString + b.asString} getOrElse {
      Variable.throwNotSupportedOperator(c, Operator.Add)
    }
  }
}


object SubApplier extends Applier  {
  override def apply(a: Variable, b: Variable)(implicit c: Cell): Any = {
    Try { a.asDouble - b.asDouble} getOrElse {
      Variable.throwNotSupportedOperator(c, Operator.Sub)
    }
  }
}
object MulApplier extends Applier  {
  override def apply(a: Variable, b: Variable)(implicit c: Cell): Any = {
    Try { a.asDouble * b.asDouble} orElse
      Try { a.asString * b.asDouble.toInt} getOrElse {
      Variable.throwNotSupportedOperator(c, Operator.Mul)
    }
  }
}
object DivApplier extends Applier  {
  override def apply(a: Variable, b: Variable)(implicit c: Cell): Any = {
    Try { a.asDouble / b.asDouble} getOrElse {
      Variable.throwNotSupportedOperator(c, Operator.Div)
    }
  }
}
object ModApplier extends Applier  {
  override def apply(a: Variable, b: Variable)(implicit c: Cell): Any = {
    Try { a.asDouble % b.asDouble} getOrElse {
      Variable.throwNotSupportedOperator(c, Operator.Mod)
    }
  }
}
