package com.geishatokyo.sqlgen.loader

import java.io.{File, InputStream}

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook

import scala.util.matching.Regex

/**
  * Created by takezoux2 on 2017/07/05.
  */
class AutoFileDetectionLoader(patterns: Seq[Pattern]) extends Loader{



  override def load(file: File): Workbook = {
    patterns.find(p => {
      p.fileRegex.findFirstIn(file.getAbsolutePath).isDefined
    }) match{
      case Some(p) => {
        p.loader.load(file)
      }
      case None => {
        throw SQLGenException(s"Can't detect loader for ${file}")
      }
    }
  }

  override def load(name: String, input: InputStream): Workbook = {
    patterns.find(p => {
      p.fileRegex.findFirstIn(name).isDefined
    }) match{
      case Some(p) => {
        p.loader.load(name, input)
      }
      case None => {
        throw SQLGenException(s"Can't detect loader for ${name}")
      }
    }
  }


  def isSupported(file: File) = {
    patterns.exists(p => {
      p.fileRegex.findFirstIn(file.getAbsolutePath).isDefined
    })
  }

}
object AutoFileDetectionLoader {

  val default: AutoFileDetectionLoader = new AutoFileDetectionLoader(
    List(
      Pattern.withExtension("csv", new CSVLoader()),
      Pattern.withExtension("xlsx", new XLSLoader()),
      Pattern.withExtension("xls", new XLSLoader())
    )
  )

}


case class Pattern(fileRegex: Regex, loader: Loader)

object Pattern {

  def withExtension(ext: String, loader: Loader) = {
    Pattern(s"""\.${ext}$$""".r, loader)
  }

}