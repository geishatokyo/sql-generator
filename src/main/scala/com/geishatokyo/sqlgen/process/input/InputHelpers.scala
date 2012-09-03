package com.geishatokyo.sqlgen.process.input

/**
 *
 * User: takeshita
 * Create: 12/07/19 0:35
 */

object InputHelpers {

  def file( f : String) = {
    SingleXLSLoader.apply(f)
  }

  def inDir(dir : String) = {
    AllInDirectoryXLSLoader.apply(dir)
  }

  /**
   * 指定したディレクトリ以下のファイルで、正規表現にマッチするファイルをリストアップする
   * @param dir
   * @param regex
   */
  def listUpFilesWithRegex(dir : String,regex : String) = {
    FileListLoader(dir,regex)
  }

}
