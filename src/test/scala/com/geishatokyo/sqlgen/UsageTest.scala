package com.geishatokyo.sqlgen

//import com.geishatokyo.sqlgen.{Context, StandardGuess, Project}
import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.meta.{ColumnMeta, Metadata, SheetMeta}
import com.geishatokyo.sqlgen.query.Query
import org.scalatest.{FlatSpec, Matchers}

/**
 * Created by takezoux2 on 15/05/04.
 */
class UsageTest extends FlatSpec with Matchers{


  it should "be used as such" in {

    val wb = new Workbook("Test")
    val s = wb.addSheet("User")
    s.addHeaders("id","nickname","age","lastLogin","gender")
    s.addRow(1,"hoge","23","2017/06/23","Male")
    s.addRow(2,"bob", 33, "2017/06/22","Female")

    val referenceWB = new Workbook("Reference")
    val s2 = referenceWB.addSheet("Hero")
    s2.addHeaders("id","name","power")
    s2.addRow(1,"Superman",100)
    s2.addRow(2,"OnePunchMan",120)

    val metadata = Metadata("MySQL",List(SheetMeta("User",List(
      ColumnMeta("id"),
      ColumnMeta("nickname"),
      ColumnMeta("age"),
      ColumnMeta("gender")
    ))))

    val graph = workbook(wb) >>
      importsWB(referenceWB) >>
      setMeta(metadata) >>
      MyProject >>
      showConsole >>
      mysql.toConsole

    println("Graph: " + graph.toString())

    graph.execute()

  }

  object Gender extends Enumeration {
    val Male = Value(1)
    val Female = Value(2)
  }


  object MyProject extends Project {

    onSheet("User") {
      column("gender").mapEnum(Gender)
      column("nickname") := "Mr. " + column("nickname")
      column("newColumn") := selectOne(Query.from("Hero").idOf(column("id")))("name")

    }


    onSheet("Hoge") {
    }

  }



}
