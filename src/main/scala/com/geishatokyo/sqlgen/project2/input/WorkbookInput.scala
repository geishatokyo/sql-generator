package com.geishatokyo.sqlgen.project2.input

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.project2.Input
import com.geishatokyo.sqlgen.process.MapContext
import java.io.File

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:52
 */
class WorkbookInput(workbook : Workbook) extends Input {
  def read() = {
    val c = new MapContext()
    c.workingDir = new File(".").getAbsolutePath
    c.name = workbook.name

    List(c -> workbook)

  }
}
