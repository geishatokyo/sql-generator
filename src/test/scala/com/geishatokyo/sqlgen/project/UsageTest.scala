package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.{Context, StandardGuess, Project}
import com.geishatokyo.sqlgen.project.input.WorkbookInput
import com.geishatokyo.sqlgen.project.output.{ConsoleOutput}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by takezoux2 on 15/05/04.
 */
class UsageTest extends FlatSpec with Matchers{

  it should "process workbook" in {
    val wb = new Workbook()
    val sheet = new Sheet("User")
    sheet.addColumns("id","name","thumb")
    sheet.addRow(List("1","Tom","tom.jpg"))
    sheet.addRow(List("2","Bob",""))
    wb.addSheet(sheet)

    val r = SampleProject(new Context(),wb)


    println("##" + r.toString)

  }
  it should "aaa" in {

    val wb = new Workbook()
    val sheet = new Sheet("User")
    sheet.addColumns("id","name","thumb")
    sheet.addRow(List("1","Tom","tom.jpg"))
    sheet.addRow(List("2","Bob",""))
    wb.addSheet(sheet)

    new WorkbookInput(wb) >> SampleProject >> (new ConsoleOutput)
  }

  object SampleProject extends Project with StandardGuess{

    onSheet("User"){implicit sheet =>

      rows.foreach(row => {
        row("id") := row("id").asInt * 2
      })

      column("aaaa") := column("id")
      column("bbbb") := {
        findById(2).map(_("name")).getOrElse("Unkonwn")
      }
      column("date") := "2016/01/01"
    }

  }


}
