package com.geishatokyo.sqlgen.project2

import org.specs2.mutable.Specification
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}

/**
 * 
 * User: takeshita
 * DateTime: 13/07/11 23:34
 */
class ProjectTest  extends Specification{

  var workbook = new Workbook()

  {
    val sheet = new Sheet("Sheet1")
    workbook.addSheet(sheet)
    sheet.addColumn("id",Nil)
    sheet.addColumn("name",Nil)
    sheet.addColumn("age",Nil)
    sheet.addRow(List("1","Tom","21"))
    sheet.addRow(List("2","Bob","22"))

  }

  {
    val sheet = new Sheet("Sheet2")
    workbook.addSheet(sheet)
    sheet.addColumn("id",Nil)
    sheet.addColumn("nickname",Nil)
    sheet.addColumn("age",Nil)
    sheet.addRow(List("1","Tommy","21"))
    sheet.addRow(List("2","Apple","22"))

  }
  class SampleProject extends DefaultProject{

    ignore( column("id") )

    newSheet("Sheet3") copy("Sheet2")

    newSheet("Sheet4").copyThenModify("Sheet2" )((s : Sheet) => {
      s.addColumns("aaa")
      s
    })

    onSheet("Sheet1"){
      ignore( column("age") )
      forColumn("name") map(v => v + v) when(_ == "Tom");
      forColumn("displayName") map(v => column("name") + column("age")) ifEmpty;
      forColumn("nickname") set({
        sheet("Sheet2").search(_("id") == column("id"))("nickname")
      })

      forColumn("age") renameTo("ageeee")

      filterRow(r => r("nickname") != "Apple")

    }

    onSheet("Sheet2"){
      renameTo("Renamed")
    }

    newSheet("EmptySheet").createEmpty("col1","col2")
  }



  "Project" should{
    "modify column values" in {

      val p = new SampleProject()

      val w = p(workbook)


      w("Sheet1").row(0)("name").value === "TomTom"


      w("Sheet1").row(0)("displayName").value === "TomTom21"
      w("Sheet1").row(0)("nickname").value === "Tommy"


    }

    "input then output" in {
      withWorkbook(this.workbook) >> new SampleProject >> console

      ok
    }
  }


}
