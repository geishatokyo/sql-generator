package com.geishatokyo.sqlgen.project3

import com.geishatokyo.sqlgen.project3.input.WorkbookInput
import com.geishatokyo.sqlgen.project3.output.{ConsoleOutput}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by takezoux2 on 15/05/04.
 */
class UsageTest extends FlatSpec with Matchers{

  it should "" in {
    val wb = new Workbook()
    val sheet = new Sheet("User")
    sheet.addColumns("id","name","thumb")
    sheet.addRow(List("1","Tom","tom.jpg"))
    sheet.addRow(List("2","Bob",""))
    wb.addSheet(sheet)

    val r = P(wb)


    println(r.toString)

  }
  it should "aaa" in {

    val wb = new Workbook()
    val sheet = new Sheet("User")
    sheet.addColumns("id","name","thumb")
    sheet.addRow(List("1","Tom","tom.jpg"))
    sheet.addRow(List("2","Bob",""))
    wb.addSheet(sheet)

    new WorkbookInput(wb) >> P >> (new ConsoleOutput)
  }

  object P extends Project with StandardGuess{

    onSheet("User"){implicit sheet =>

      rows.foreach(row => {
        row("id") := row("id").asInt * 2
      })

      column("aaaa") := column("id")
      column("bbbb") := {
        rows.find(r => r("id") ~== "2").get.apply("name")
      }
    }

  }


}
