package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.Context

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:06
 */
trait Output {

  def write(context : Context,w : Workbook) : Unit

  def +(output : Output) : Output = {
    output match{
      case OutputList(outputs) => OutputList(this :: outputs)
      case _ => OutputList(this :: output :: Nil)
    }
  }
}

case class OutputList(outputs : List[Output]) extends Output{


  def write(context: Context, w: Workbook) {
    outputs.foreach(o => {
      o.write(context,w)
    })
  }

  override def +(output : Output) : Output = {
    output match{
      case OutputList(outputs) => OutputList(this.outputs ::: outputs)
      case _ => OutputList(this.outputs :+ output)
    }
  }
}
