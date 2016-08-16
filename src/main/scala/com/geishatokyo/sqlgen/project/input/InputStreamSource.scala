package com.geishatokyo.sqlgen.project.input

import java.io.InputStream

import com.geishatokyo.sqlgen.util.FileUtil

/**
  * Created by takezoux2 on 2016/08/09.
  */
class InputStreamSource(stream: InputStream,closeAfterDone: Boolean = true) {


  def asXls() = try{
    val wb = XLSLoader.load(stream)
    new WorkbookInput(wb)
  }finally{
    if(closeAfterDone) stream.close()
  }

  def asCsv() = try{
    val data = new Array[Byte](stream.available())
    stream.read(data)
    new CSVInput(List(new String(data,"utf-8")))
  }finally{
    if(closeAfterDone) stream.close()
  }



}

