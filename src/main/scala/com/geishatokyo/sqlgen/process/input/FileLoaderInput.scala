package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.{AutoFileDetectionLoader, CSVLoader, Loader}
import com.geishatokyo.sqlgen.process.{Context, EmptyProc, InputProc, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */

class FileLoaderInput(val loader: Loader,
                      _workingDir: String,
                      val filter: File => Boolean,
                      val fileOrDirs: Seq[String]) extends InputProc with FileListUpSupport{



  override def workingDir: Option[String] = {
    Some(_workingDir)
  }
}

object FileLoaderInput {

  def auto(files: String*): Proc = {
    if(files.size == 0) return EmptyProc

    val dir = if(new File(files.head).isFile) new File(files.head).getParent
    else files.head

    new FileLoaderInput(AutoFileDetectionLoader.default, dir,f => {
      f.getName.endsWith(".csv") ||
      f.getName.endsWith(".xls") ||
      f.getName.endsWith(".xlsx")
    }, files)
  }

  def csv(files: String*): Proc = {
    if(files.size == 0) return EmptyProc
    val dir = new File(files.head).getParent
    new FileLoaderInput(new CSVLoader(), dir,f => f.getName.endsWith(".csv"), files)
  }



}
