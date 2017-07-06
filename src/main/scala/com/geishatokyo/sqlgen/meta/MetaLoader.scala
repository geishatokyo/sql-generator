package com.geishatokyo.sqlgen.meta

import java.io.{File, FileInputStream, InputStream}


/**
  * Created by takezoux2 on 2017/07/05.
  */
trait MetaLoader {

  def load(path: String) : Metadata = load(new File(path))
  def load(file: File): Metadata = load(new FileInputStream(file))
  def load(input: InputStream): Metadata

}
