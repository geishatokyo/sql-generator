package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.logger.Logger

/**
  * Created by takezoux2 on 2017/06/30.
  */

trait Proc  {

  def apply(c: Context): Context

  def thisProc: Proc = this

  def >>(proc: Proc): Proc = {
    ProcNode(thisProc,proc)
  }

  def >>(procs: (Proc,Proc)): (Proc,Proc) = {
    (ProcNode(thisProc, procs._1), ProcNode(thisProc, procs._2))
  }


  def >>(procs: (Proc,Proc,Proc)): (Proc,Proc,Proc) = {
    (ProcNode(thisProc, procs._1),
      ProcNode(thisProc, procs._2),
      ProcNode(thisProc, procs._3)
    )
  }

  /**
    * 再計算を防ぎパフォーマンスを向上します
    * 副作用が発生しないことが保証されてない場合、結果にさい差異が生まれる可能性があります。
    * @return
    */
  def cached = {
    CachedProc(this)
  }

  def execute(): Context = {
    execute(new DefaultContext())
  }

  def execute(c: Context): Context = {
    // Contextをコピーし、他のProcessからの影響を消す
    val newC = c.copy()
    thisProc.apply(newC)
  }


  override def toString() = {
    getClass.getSimpleName.stripSuffix("Proc")
  }

}

object EmptyProc extends Proc {
  override def apply(c: Context): Context = {
    c
  }
}

case class ProcNode(beforeProc: Proc, currentProc: Proc) extends Proc {

  override def apply(c: Context): Context = {
    currentProc.apply(c)
  }

  override def execute(c: Context): Context = {
    val c2 = beforeProc.execute(c)
    currentProc.execute(c2)
  }


  override def toString(): String = {
    beforeProc.toString() + " >> " + currentProc.toString()
  }

}

case class CachedProc(proc: Proc) extends Proc {

  override def apply(c: Context): Context = {
    proc.apply(c)
  }

  private var result:Option[Context] = None

  override def execute(c: Context): Context = {
    result match {
      case Some(cachedContext) => {
        Logger.log(s"Use cache for: ${proc.toString()}")
        cachedContext
      }
      case None => {
        val c2 = proc.execute(c)
        result = Some(c2)
        c2
      }
    }
  }

  override def toString(): String = {
    s"Cached(${proc.toString()})"
  }


}