package com.geishatokyo.sqlgen.project2.input

import com.geishatokyo.sqlgen.project2.Input
import java.io.File
import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.process.MapContext

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:34
 */
class XLSFileInput(file : String) extends Input {
  def read() = {

    val context = new MapContext()
    val (dir,name,ext) = FileUtil.splitPathAndNameAndExt(file)
    context.workingDir = dir
    context.name = name

    List(context -> XLSLoader.load(file))
  }
}
