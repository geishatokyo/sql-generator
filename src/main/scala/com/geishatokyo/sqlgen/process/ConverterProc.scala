package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.process.output.{ConsoleOutputProc, FileOutputProc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait ConverterProc[T] extends Proc {

  def dataKey: Key[MultiData[T]]

  def convert(c: Context): MultiData[T]

  def toConsole : Proc = {
    (this >> new ConsoleOutputProc(dataKey))
  }
  def toDir(dir: String): Proc = {
    this >> new FileOutputProc(dir, dataKey)
  }


  override def apply(c: Context): Context = {
    if(c.has(dataKey)){
      c
    } else {
      val d = convert(c)
      c(dataKey) = d
      c
    }
  }
}


