package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.loader.{AutoFileDetectionLoader, Loader}

/**
  * Created by takezoux2 on 2017/07/07.
  */
class FileImportProc(val loader: Loader,
                     val filter: File => Boolean,
                     val fileOrDirs: Seq[String]) extends LoaderImportProc with FileListUpSupport {

}


object FileImportProc {

  def auto(fileOrDirs: String*) = {
    new FileImportProc(AutoFileDetectionLoader.default, _ => true, fileOrDirs)
  }
}