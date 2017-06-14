package com.geishatokyo.sqlgen.loader

/**
  * Created by takezoux2 on 2017/06/14.
  */
trait Source[T] {


  def load() : T

}
