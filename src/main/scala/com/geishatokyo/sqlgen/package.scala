package com.geishatokyo

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.generator.code.csharp.CSharpCodeGenerator
import com.geishatokyo.sqlgen.generator.sql.QueryType
import com.geishatokyo.sqlgen.generator.sql.sqlite.SqliteQueryGenerator
import com.geishatokyo.sqlgen.process.converter.code.CSharpCodeConverterProc
import com.geishatokyo.sqlgen.process.converter.csv.CSVConverterProc
import com.geishatokyo.sqlgen.process.converter.sql.{MySQLConverterProc, SqliteConverterProc}
import com.geishatokyo.sqlgen.process.input.{FileImportProc, FileLoaderInput, WorkbookImportProc, WorkbookInput}
import com.geishatokyo.sqlgen.process.output.ConsoleOutputProc
import com.geishatokyo.sqlgen.process.{Proc, ProjectProc}

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
    * ファイルからWorkbookを読み込む
    * @param file
    * @return
    */
  def fromFile(file: String*): Proc = {
    FileLoaderInput.auto(file:_*)
  }

  /**
    * ディレクトリ内のWorkbookを全て読み込む
    * @param dir
    * @return
    */
  def inDir(dir: String): Proc = {
    FileLoaderInput.auto(dir)
  }

  /**
    * 指定したWorkbookを読み込む
    * @param w
    * @return
    */
  def workbook(w: Workbook): Proc = {
    new WorkbookInput(w)
  }

  /**
    * 参照(ReadOnly)としてWorkbookをファイルから読み込む
    * @param fileOrDirs
    * @return
    */
  def imports(fileOrDirs: String*): Proc = {
    FileImportProc.auto(fileOrDirs:_*)
  }
  def importsWB(wbs: Workbook*): Proc = {
    wbs.map(new WorkbookImportProc(_)).reduceLeft[Proc]((a,b) => a >> b)
  }


  implicit def projectProc(p: Project): Proc = {
    new ProjectProc(p)
  }

  /**
    * CSV形式へ変換する
    * @return
    */
  def csv = {
    new CSVConverterProc()
  }

  /**
    * MySQLのクエリへ変換する
    * @return
    */
  def mysql = {
    new MySQLConverterProc(QueryType.Replace)
  }

  /**
    * Sqliteのクエリへ変換する
    * @return
    */
  def sqlite = {
    new SqliteConverterProc(QueryType.Replace)
  }


  /**
    * ConsoleにCSV形式で出力する
    * @return
    */
  def showConsole = {
    csv.toConsole
  }



}