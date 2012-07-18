package com.geishatokyo.sqlgen

import sheet.Workbook

/**
 *
 * User: takeshita
 * Create: 12/07/18 18:05
 */

trait DataLoader[-ProjectType <: Project] {

  def load(project : ProjectType, context : Context) : Workbook

}
