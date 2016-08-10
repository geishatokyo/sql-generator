package com.geishatokyo

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import com.geishatokyo.sqlgen.catalog.{WorkbookImporter, WorkbooKMerger}
import com.geishatokyo.sqlgen.project.flow.Output
import com.geishatokyo.sqlgen.project.input.{FileInput}
import com.geishatokyo.sqlgen.project.output.{ConsoleOutput, SQLOutput, XlsOutput}

/**
  * usage:
  *
  * file("hoge.xls",
  *
  *
 * Created by takezoux2 on 15/05/05.
 */
package object sqlgen {


  /**
    * 指定したファイルまたはディレクトリに含まれるファイル全てをWorkbookに読み込む
    * 拡張子は xls,xlsx,csvのいずれか
    * @param file
    * @return
    */
  def file(file: String) = {
    new FileInput(new File(file))
  }

  /**
    * 指定した複数のファイル、またはディレクトリに含まれるファイルを全てWorkbookに読み込む
    * 拡張子は xls,xlsx,csvのいずれか
    * @param fs
    * @return
    */
  def files(fs: List[String]) = {
    new FileInput(fs.map(new File(_)):_*)
  }

  /**
    * 指定したパス下のディレクトリーのうち、名前順で一番最後のディレクトリ内のファイルだけを読み込む
    * 使用例:日付をディレクトリ名にして、最新のディレクトリ内のものだけ処理を行う
    * @param d
    * @return
    */
  def lastDirIn(d: String) = {
    val file = new File(d)
    val dir = file.listFiles().filter(f => !f.isHidden && f.isDirectory).sortBy(_.getName).last
    new FileInput(dir)
  }

  /**
    * 複数のWorkbookが読み込まれた場合に、統合する
    * @return
    */
  def merge = new WorkbooKMerger

  /**
    * 参照するWorkbookを読み込む
    * @param pathes
    * @return
    */
  def imports(pathes : String *) = {
    new WorkbookImporter(pathes.map(p => new File(p)):_*)
  }


  /**
    * MySQLのクエリを出力する
    */
  def asMySQL = {
    {
      def toDir(dirPath: String) = {
        val output = SQLOutput.mysql()
        output.path = dirPath
        output
      }

    }
  }

  /**
    * Sqliteのクエリを出力する
    */
  def asSqlite = {
    {
      def toDir(dirPath: String) = {
        val output = SQLOutput.sqlite()
        output.path = dirPath
        output
      }
    }
  }

  /**
    * XLSファイルとして出力する
    */
  def asXLS = {
    {
      def toDir(dirPath: String) =
        new XlsOutput(dirPath,false)
    }
  }

  /**
    * XLSXファイルとして出力する
    * @param dirPath
    */
  def asXLSX(dirPath : String) = {
    {
      def toDir(dirPath: String) =
        new XlsOutput(dirPath,true)
    }
  }

  /**
    * コンソールに出力する
    * @return
    */
  def console : Output = {
    new ConsoleOutput
  }

}
