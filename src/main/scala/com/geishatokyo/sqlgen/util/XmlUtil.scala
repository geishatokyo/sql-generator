package com.geishatokyo.sqlgen.util

import xml.{Node, PrettyPrinter}
import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: t_izumi
 * Date: 12/06/21
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */

object XmlUtil {

  def printXml(xml: Node, target: File) = {
    val pp = new PrettyPrinter(200, 2)
    val s = pp.format(xml)
    FileUtil.saveTo(target.getAbsolutePath, List(s))
  }

}
