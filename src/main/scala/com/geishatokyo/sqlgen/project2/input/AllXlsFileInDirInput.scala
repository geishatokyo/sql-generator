package com.geishatokyo.sqlgen.project2.input

import java.io.File
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.project2.Input
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.process.MapContext
import com.geishatokyo.sqlgen.sheet.Workbook

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:33
 */


class AllXlsFileInDirInput(dir : String) extends Input{
  def read() : List[(Context,Workbook)] = {
    val d = new File(dir)

    if (d.listFiles() == null){
      return Nil
    }

    d.listFiles().withFilter( f => {
      f.getAbsolutePath.endsWith(".xls")
    }).map( f => {
      val context = new MapContext()
      val (dir,name,ext) = FileUtil.splitPathAndNameAndExt(f.getAbsolutePath)
      context.workingDir = dir
      context.name = name

      context -> XLSLoader.load(f)
    }).toList

  }
}
