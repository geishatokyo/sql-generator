package com.geishatokyo.sqlgen.project.input

import java.io.File

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by takezoux2 on 2016/08/09.
  */
class FileFindTest extends FlatSpec with Matchers {


  it should "load specific file extensions" in {
    val fs = new FileSource(new File("src/test/resources"))

    val files = fs.listUpFiles(List("xls"))

    files.foreach(f => println(f.getAbsolutePath))

    assert(files.forall(_.getName.endsWith(".xls")))
  }

  it should "" in {
    val file = new File("src/test/resources")
    val dir = file.listFiles().filter(f => !f.isHidden && f.isDirectory).sortBy(_.getName).last

    println(dir.getAbsolutePath)
  }

}
