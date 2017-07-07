package com.geishatokyo.sqlgen

import java.io.File
import java.nio.file.Paths

import com.geishatokyo.sqlgen.process.Context
import com.geishatokyo.sqlgen.util.FileUtil

/**
  * Created by takezoux2 on 2017/07/07.
  */
trait IOSupport { self: Project =>

  def getMediaPath(path: String) = {
    if(context.has(Context.WorkingDir)) {
      Paths.get( context.workingDir, path).toFile
    }else new File(path)
  }

  def readText(path: String) = {
    FileUtil.loadFileAsString(getMediaPath(path))
  }


}
