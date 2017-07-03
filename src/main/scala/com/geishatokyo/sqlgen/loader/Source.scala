package com.geishatokyo.sqlgen.loader

import java.io.FileInputStream

/**
  * Created by takezoux2 on 2017/06/14.
  */
trait Source[+T] {

  def name: String
  def load() : T

}


case class FileToStringSource(name: String,files: Iterable[String]) extends Source[Iterable[String]] {
  override def load() = {
    files.map(f => {
      val input = new FileInputStream(f)
      try {
        val bytes = new Array[Byte](input.available())
        input.read(bytes)
        new String(bytes, "utf-8")
      }finally{
        input.close()
      }
    })
  }
}

case class StringListSource(name: String, data: Iterable[String]) extends Source[Iterable[String]] {
  override def load(): Iterable[String] = data
}