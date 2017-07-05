package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.{AutoFileDetectionLoader, Loader}
import com.geishatokyo.sqlgen.process.Context

/**
  * Created by takezoux2 on 2017/07/05.
  */
class DirectoryLoaderInput(
                            val loader: Loader,
                            dir: String,
                            includeChildDirs: Boolean,
                            filter: File => Boolean) extends LoaderInput{
  override def load(c: Context): Workbook = {
    val current = new File(dir)
    val yielder = new Yielder()
    if(current.listFiles() != null) {
      current.listFiles().foreach(f => {
        listUp(f, yielder.apply _)
      })
    }
    loader.loadMultiFile(yielder.elements.reverseIterator)
  }

  class Yielder{

    var elements = List.empty[File]

    def apply(f: File): Unit = {
      elements = f :: elements
    }

  }


  private def listUp(currentDir: File, yieldFunc: File => Unit): Unit = {
    if(currentDir.isFile) {
      if(filter(currentDir)) {
        yieldFunc(currentDir)
      }
    }else if(includeChildDirs){
      if(currentDir.listFiles() != null) {
        currentDir.listFiles().foreach(f => {
          listUp(f, yieldFunc)
        })
      }
    }
  }



  override def workingDir: Option[String] = Some(dir)
}

object DirectoryLoaderInput {

  def auto(dir: String) = {
    val loader = AutoFileDetectionLoader.default
    new DirectoryLoaderInput(
      loader,
      dir,
      true,
      f => loader.isSupported(f)
    )
  }


}