package com.geishatokyo.sqlgen.loader

import java.io.{File, InputStream}

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook

import scala.util.matching.Regex

/**
  * Created by takezoux2 on 2017/07/05.
  */
class AutoFileDetectionLoader(patterns: Seq[Pattern], _defaultLoader : Option[Loader] = None) extends Loader{


  def defaultLoader: Loader = _defaultLoader orElse
    patterns.headOption.map(_.loader) getOrElse {
    throw new SQLGenException("No default loader")
  }

  override def load(file: File): Workbook = {
    patterns.find(p => {
      p.fileRegex.findFirstIn(file.getAbsolutePath).isDefined
    }) match{
      case Some(p) => {
        p.loader.load(file)
      }
      case None => {
        defaultLoader.load(file)
      }
    }
  }

  override def load(name: String, input: InputStream): Workbook = {
    defaultLoader.load(name, input)
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
      Pattern.withExtension("csv", new CSVLoader())
    )
  )

}


case class Pattern(fileRegex: Regex, loader: Loader)

object Pattern {

  def withExtension(ext: String, loader: Loader) = {
    Pattern(s"""\.${ext}$$""".r, loader)
  }

}