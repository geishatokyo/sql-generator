package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook
import input.XLSLoader
import java.io.File
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.process.input.SingleXLSLoader
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.sheet.load.hssf.XLSSheetLoader

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:03
 */
trait Input {

  def >>(project : Project) : Mediator = {
    new Mediator(this,project)
  }

  def read() : List[(Context,Workbook)]

}

