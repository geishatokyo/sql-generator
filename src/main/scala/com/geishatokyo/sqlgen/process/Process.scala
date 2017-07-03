package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/06/30.
  */

trait Proc extends Function2[Context,Workbook,Workbook]


trait Context
{

  def get[T](key: String): Option[T]

}