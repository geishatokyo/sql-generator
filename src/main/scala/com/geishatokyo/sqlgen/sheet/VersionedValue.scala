package com.geishatokyo.sqlgen.sheet

/**
 *
 * User: takeshita
 * Create: 12/07/11 21:29
 */

class VersionedValue(val initialValue : String) {

  protected def this(values : List[String]) = {
    this(values.last)
    this.values = values
  }

  protected var values : List[String] = initialValue :: Nil

  def apply() = value
  def apply(v : String) = this.value = v

  def :=(v : String) = this.value = v

  def value = values.head

  def value_=(v : String) {
    values = v :: values
  }

  def valueOf(index : Int) = values(index)

  /**
   * compare string case insensitively
   * @param v
   */
  def =~=(v : String) = {
    val thisV = value
    if (v == null && thisV == null) true
    else if( v == null || thisV == null) false
    else v.toLowerCase == thisV.toLowerCase
  }

  def empty_? = {
    val v = this.value
    v == null || v.length == 0
  }


  def copy() : this.type = {
    new VersionedValue(values).asInstanceOf[this.type]
  }

  def historySize = values.size

  override def toString: String = value

  override def equals(obj: Any): Boolean = {
    obj match{
      case s : VersionedValue => s.value == this.value
      case s : String => s == this.value
      case v => v.toString == this.value
    }
  }
}

object VersionedValue{
  implicit def toString(v : VersionedValue) = {
    v.toString
  }
}