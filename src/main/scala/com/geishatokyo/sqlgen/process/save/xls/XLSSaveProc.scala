package com.geishatokyo.sqlgen.process.save.xls

import java.io.FileOutputStream

import com.geishatokyo.sqlgen.process.{Context, OutputSupport, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class XLSSaveProc(dir: String) extends Proc with XLSConverter with OutputSupport{

  def isXlsx = true

  def extension = if(isXlsx) ".xlsx" else ".xls"

  override def apply(c: Context): Context = {
    val xlsWb = toHSSFSheet(c.workbook, isXlsx)
    val path = getPath(c, dir, c.workbook.name + extension)
    val output = new FileOutputStream(path)
    try {
      xlsWb.write(output)
    }finally {
      output.close()
    }
    c
  }
}
