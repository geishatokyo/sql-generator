package com.geishatokyo.sqlgen

import scala.collection.mutable

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:44
 */

trait Context extends mutable.Map[String,Any] {

  val WorkingDir = "WorkingDir"
  val Name = "Name"

  def workingDir : String = getAsString(WorkingDir,"")
  def workingDir_=(v : String) = this +=(WorkingDir -> v)
  def name : String = getAsString(Name,"")
  def name_=( v : String) = this +=(Name -> v)

  def getAsString(key : String , dflt : => String) : String = {
    get(key).map(_.toString).getOrElse(dflt)
  }
  def getAsInt(key : String , dflt : => Int) : Int = {
    get(key).map(_.toString.toInt).getOrElse(dflt)
  }
  def getAsBool(key : String , dflt : => Boolean) : Boolean = {
    get(key).map(_.toString.toBoolean).getOrElse(dflt)
  }

}
