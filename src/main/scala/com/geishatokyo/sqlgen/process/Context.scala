package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook
import scala.collection.mutable

/**
  * Created by takezoux2 on 2017/07/05.
  */


case class Key[+T](key: String) {
  override def toString: String = key
}

trait Context
{
  def workbook: Workbook = apply(Context.Workbook)
  def workingDir : String = apply(Context.WorkingDir)

  def hasWorkbook = has(Context.Workbook)

  def apply[T](key: Key[T]): T = {
    get(key).getOrElse {
      throw SQLGenException(s"'${key}' is not set in Context")
    }
  }
  def has(key: Key[_]): Boolean = get(key).isDefined
  def get[T](key: Key[T]): Option[T]
  def update[T](key: Key[T], a:T): Unit

}


object Context {
  val Workbook = Key[Workbook]("workbook")
  val WorkingDir = Key[String]("workingDir")

  val Import = Key[List[Workbook]]("import")

  val ExportDir  = Key[String]("exportDir")
}


class DefaultContext extends Context{

  val values = mutable.Map.empty[String,Any]

  override def get[T](key: Key[T]): Option[T] = values.get(key.key).asInstanceOf[Option[T]]

  override def update[T](key: Key[T], a: T): Unit = values(key.key) = a
}
