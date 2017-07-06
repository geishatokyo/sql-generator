package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook
import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/07/05.
  */



trait Context
{
  def workbook: Workbook = apply(Context.Workbook)
  def workingDir : String = apply(Context.WorkingDir)

  def hasWorkbook = has(Context.Workbook)

  def apply[T](key: String): T = {
    get(key).getOrElse {
      throw SQLGenException(s"'${key}' is not set in Context")
    }
  }
  def has(key: String): Boolean = get(key).isDefined
  def get[T](key: String): Option[T]
  def update(key: String, a:Any): Unit

}


object Context {
  val Workbook = "workbook"
  val WorkingDir = "workingDir"

  val ExportDir  ="exportDir"
}


class DefaultContext extends Context{

  val values = mutable.Map.empty[String,Any]

  override def get[T](key: String): Option[T] = values.get(key).asInstanceOf[Option[T]]

  override def update(key: String, a: Any): Unit = values(key) = a
}
