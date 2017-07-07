package com.geishatokyo.sqlgen.process.output

import java.io.{File, FileOutputStream}
import java.nio.file.Paths

import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.process.{Context, Key, MultiData, OutputProc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
class FileOutputProc(dir: String, val dataKey: Key[MultiData[Any]]) extends OutputProc[Any] {


  override def output(data: MultiData[Any], c: Context): Unit = {
    data.datas.foreach(d => {
      val path = getPath(c, dir, d.name)
      if(!path.getParentFile.exists()){
        path.getParentFile.mkdirs()
      }
      val output = new FileOutputStream(path)
      try {
        Logger.log("Save " + path)
        output.write(d.asBytes)
      } finally {
        output.flush()
        output.close()
      }

    })
  }
}
