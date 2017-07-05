package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.{AutoFileDetectionLoader, CSVLoader, Loader}
import com.geishatokyo.sqlgen.process.{Context, EmptyProc, InputProc, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */

class FileLoaderInput(val loader: Loader,_workingDir: String, files: String*) extends InputProc {

  override def load(c: Context): Workbook = {
    loader.loadMulti(files.iterator)
  }

  override def workingDir: Option[String] = {
    Some(_workingDir)
  }
}

object FileLoaderInput {

  def auto(files: String*): Proc = {
    if(files.size == 0) return EmptyProc
    val dir = new File(files.head).getParent
    new FileLoaderInput(AutoFileDetectionLoader.default, dir, files:_*)
  }

  def csv(files: String*): Proc = {
    if(files.size == 0) return EmptyProc
    val dir = new File(files.head).getParent
    new FileLoaderInput(new CSVLoader(), dir, files:_*)
  }



}
