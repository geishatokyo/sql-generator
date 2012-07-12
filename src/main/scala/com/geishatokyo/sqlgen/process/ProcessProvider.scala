package com.geishatokyo.sqlgen.process

import com.geishatokyo.sqlgen.{Context, Project}
import com.geishatokyo.sqlgen.logger.Logger

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:43
 */

trait ProcessProvider {

  val logger = Logger

  type ProjectType <: Project

  val project : ProjectType
  def context : Context
}
