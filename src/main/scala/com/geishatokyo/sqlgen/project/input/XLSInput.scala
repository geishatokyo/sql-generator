package com.geishatokyo.sqlgen.project.input

import java.io.File

import com.geishatokyo.sqlgen.process.{Context, DefaultContext}
import com.geishatokyo.sqlgen.project.flow.{Input, InputData}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
  * Created by takezoux2 on 2016/08/08.
  */
class XLSInput(files: List[File]) extends Input {


  override def read(): List[InputData] = {

    files.map(f => {
      val wb = XLSLoader.load(f)
      val context = new DefaultContext()
      context("hoge") = f.getParent
      InputData(context,wb)
    })
  }
}
