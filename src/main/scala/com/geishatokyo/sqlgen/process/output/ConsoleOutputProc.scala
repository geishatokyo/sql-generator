package com.geishatokyo.sqlgen.process.output

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class ConsoleOutputProc extends Proc {

  override def apply(c: Context): Context = {
    val wb = c.workbook
    println(toString(wb))
    c
  }

  def toString(wb: Workbook) = {
    val builder = new StringBuilder()

    def appendLn(s: String) = builder.append(s + "\n")

    appendLn("# @Workbook " + wb.name)
    wb.sheets.foreach(s => {
      appendLn(s"# @Sheet ${s.name}")
      appendLn(s.headers.map(_.name).mkString(","))
      s.rows.foreach(row => {
        appendLn(row.cells.map(_.asString).mkString(","))
      })

      appendLn("")
    })

    builder.toString()

  }


}
