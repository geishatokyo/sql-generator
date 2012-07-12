package com.geishatokyo.sqlgen.util

import xml.{UnprefixedAttribute, Elem}

/**
 * Created with IntelliJ IDEA.
 * User: t_izumi
 * Date: 12/06/21
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */

case class XmlAttributeBuilder(e: Elem) {

  private var newElem = e

  def add(key: String, value:Any) = {
    newElem = newElem % new UnprefixedAttribute(key, value.toString, newElem.attributes)
    this
  }

  def update(key: String, f:String => Any) = {
    val old = newElem \ ("@" + key)
    val newText = f(old.text).toString
    newElem = newElem % new UnprefixedAttribute(key, newText, newElem.attributes.filter(_.key != key))
    this
  }

  def build = newElem

}
