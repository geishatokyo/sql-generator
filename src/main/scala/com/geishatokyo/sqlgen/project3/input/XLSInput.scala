package com.geishatokyo.sqlgen.project3.input

import java.io.File

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project3.flow.Input
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/08.
  */
class XLSInput(files: List[File]) extends Input {
  override def read(): (Context,Workbook) = {
    val workbooks = files.map(f => {
      XLSLoader.load(f)
    })

    val wb = if(workbooks.size == 0) new Workbook()
    else if(workbooks.size == 1) workbooks.head
    else {
      workbooks.reduce[Workbook]((w1,w2) => {
        w2.sheets.foreach(s => {
          w1.addSheet(s)
        })
        w1
      })
    }

    val context = new Context()
    files.headOption.foreach(f => {
      context.workingDir = f.getParent
    })

    (context,wb)
  }
}
