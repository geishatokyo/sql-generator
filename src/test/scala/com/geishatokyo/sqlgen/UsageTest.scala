package com.geishatokyo.sqlgen

//import com.geishatokyo.sqlgen.{Context, StandardGuess, Project}
import com.geishatokyo.sqlgen.core.Workbook
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by takezoux2 on 15/05/04.
 */
class UsageTest extends FlatSpec with Matchers{


  it should "" in {

    val wb = new Workbook("Test")
    val s = wb.addSheet("User")
    s.addHeaders("id","nickname","age","lastLogin")
    s.addRow(1,"hoge","23","2017/06/23")
    s.addRow(2,"bob", 33, "2017/06/22")

    println("Graph: " + (workbook(wb) >> MyProject >> showConsole).toString())

    workbook(wb) >> MyProject >> showConsole execute

  }



  object MyProject extends Project {

    onSheet("User") {
      column("id") := { column("id").asLong * 100 + column("age").asLong}
      column("nickname") := "Mr. " + column("nickname")
    }
  }


//
//  it should "process workbook" in {
//    val wb = new Workbook()
//    val sheet = new Sheet("User")
//    sheet.addColumns("id","name","thumb")
//    sheet.addRow(List("1","Tom","tom.jpg"))
//    sheet.addRow(List("2","Bob",""))
//    wb.addSheet(sheet)
//
//    val r = SampleProject(new Context(),wb)
//
//
//    println("##" + r.toString)
//
//  }
//  it should "aaa" in {
//
//    val wb = new Workbook()
//    val sheet = new Sheet("User")
//    sheet.addColumns("id","name","thumb")
//    sheet.addRow(List("1","Tom","tom.jpg"))
//    sheet.addRow(List("2","Bob",""))
//    wb.addSheet(sheet)
//
//    new WorkbookInput(wb) >> SampleProject >> (new ConsoleOutput)
//  }
//
//  object SampleProject extends Project with StandardGuess{
//
//    onSheet("User"){
//
//      rows.foreach(row => {
//        row("id") := row("id").asInt * 2
//      })
//
//      column("aaaa") := column("id")
//      column("bbbb") := {
//        findById(2).map(_("name")).getOrElse("Unkonwn")
//      }
//      column("date") := "2016/01/01"
//    }
//
//  }


}
