package com.geishatokyo.sqlgen.process.converter.csv

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.process._

/**
  * Created by takezoux2 on 2017/07/05.
  */
class CSVConverterProc extends ConverterProc[String]{

  override def dataKey = Key("result.csv")

  override def convert(c: Context): MultiData[String] = {
    val w = c.workbook
    MultiData(
      StringData(w.name, toString(w))
    )
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
