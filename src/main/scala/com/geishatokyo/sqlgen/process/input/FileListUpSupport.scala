package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.Loader
import com.geishatokyo.sqlgen.process.{Context, Proc}

/**
  * Created by takezoux2 on 2017/07/07.
  */
trait FileListUpSupport { self: Proc =>

  def excludeDirs: Set[String]
  def fileOrDirs: Seq[String]
  def filter: File => Boolean
  def loader: Loader


  def load(c: Context): Option[Workbook] = {

    val yielder = new Yielder()
    fileOrDirs.foreach(path => {
      val f = new File(path)
      listUp(f, yielder.apply _)
    })

    if(yielder.elements.size > 0) {
      Some(loader.loadMultiFile(yielder.elements.iterator))
    } else {
      None
    }
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
    }else if(currentDir.listFiles() != null) {
      if(isTargetDir(currentDir)) {
        currentDir.listFiles().foreach(f => {
          listUp(f, yieldFunc)
        })
      }
    }

  }
  protected def isTargetDir(dir: File) = {
    !excludeDirs.contains(dir.getName) &&
      !dir.getName.startsWith(".") &&
      !dir.isHidden
  }

}
