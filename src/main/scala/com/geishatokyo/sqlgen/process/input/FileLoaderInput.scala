package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.{AutoFileDetectionLoader, CSVLoader, Loader}
import com.geishatokyo.sqlgen.process.{Context, EmptyProc, InputProc, Proc}
import com.geishatokyo.sqlgen.setting.SettingLoader

import scala.util.matching.Regex

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
      !isExcludeFile(f) && isTargetFile(f)
    }, files)
  }

  def csv(files: String*): Proc = {
    if(files.size == 0) return EmptyProc
    val dir = new File(files.head).getParent
    new FileLoaderInput(new CSVLoader(), dir,f => f.getName.endsWith(".csv"), files)
  }

  lazy val isExcludeFile: File => Boolean = {
    SettingLoader.getString("SG_EXCLUDE_FILE_REGEX") match{
      case Some(regex) => {
        isMatchRegex(regex.r) _
      }
      case _ => DefaultRule.isBackUpFile _
    }
  }

  lazy val isTargetFile: File => Boolean = {
    SettingLoader.getString("SG_TARGET_FILE_REGEX") match{
      case Some(regex) => {
        isMatchRegex(regex.r) _
      }
      case _ => f => DefaultRule.isAnyXLS(f) || DefaultRule.isAnyCSV(f)
    }
  }

  def isMatchRegex(r: Regex)(f: File) = r.findFirstIn(f.getName).isDefined


  object DefaultRule {
    def isBackUpFile(f: File) = {
      f.getName.startsWith("~") //エクセルのバックアップファイル
    }

    def isAnyCSV(f: File) = {
      f.getName.endsWith(".csv")
    }

    def isAnyXLS(f: File) = {
      f.getName.endsWith(".xlsx") ||
        f.getName.endsWith(".xlsm") || // マクロ入り
        f.getName.endsWith(".xls")
    }
  }


}
