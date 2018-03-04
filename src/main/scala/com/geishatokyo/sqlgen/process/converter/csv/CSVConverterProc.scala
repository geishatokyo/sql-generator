package com.geishatokyo.sqlgen.process.converter.csv

import com.geishatokyo.sqlgen.core.{Sheet, Workbook}
import com.geishatokyo.sqlgen.process._

/**
  * Created by takezoux2 on 2017/07/05.
  */
class SingleFileCSVConverterProc extends ConverterProc[String]{

  override def dataKey = Key("single_file.csv")

  override def convert(c: Context): MultiData[String] = {
    val w = c.workbook
    MultiData(
      StringData(w.name + ".csv", toString(w))
    )
  }
  def toString(wb: Workbook) = {
    val builder = new StringBuilder()

    def appendLn(s: String) = builder.append(s + "\n")

    appendLn("# @Workbook " + wb.name)
    wb.sheets.filterNot(_.isIgnore).foreach(s => {
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

class SheetSeparatedCSVConverterProc extends ConverterProc[String]{

  override def dataKey = Key("sheet_separated.csv")

  override def convert(c: Context): MultiData[String] = {
    val w = c.workbook
    MultiData(
      w.sheets.map(sheet => {
        StringData(s"${sheet.name}.csv", toString(sheet))
      }):_*
    )
  }
  def toString(sheet: Sheet) = {
    val builder = new StringBuilder()

    def appendLn(s: String) = builder.append(s + "\n")

    appendLn(s"# @Sheet ${sheet.name}")
    appendLn(sheet.headers.map(_.name).mkString(","))
    sheet.rows.foreach(row => {
      appendLn(row.cells.map(_.asString).mkString(","))
    })

    appendLn("")

    builder.toString()

  }

}


