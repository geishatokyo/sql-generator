package com.geishatokyo.sqlgen.project.input

import java.io.File

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by takezoux2 on 2016/08/09.
  */
class FileFindTest extends FlatSpec with Matchers {


  it should "load specific file extensions" in {
    val fs = new FileInput(new File("src/test/resources"))

    val files = fs.listUpFiles()

    files.foreach(f => println(f.getAbsolutePath))

    assert(files.forall(f => {
      val name = f.getName
      name.endsWith(".xls") ||
      name.endsWith(".csv") ||
      name.endsWith(".xlsx")
    }))
  }

  it should "" in {
    val file = new File("src/test/resources")
    val dir = file.listFiles().filter(f => !f.isHidden && f.isDirectory).sortBy(_.getName).last

    println(dir.getAbsolutePath)
  }

}
