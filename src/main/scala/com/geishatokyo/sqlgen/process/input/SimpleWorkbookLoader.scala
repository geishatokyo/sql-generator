package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.{Project, Context, DataLoader}
import com.geishatokyo.sqlgen.sheet.Workbook

/**
 *
 * User: takeshita
 * Create: 12/07/19 0:18
 */

class SimpleWorkbookLoader(workbook : Workbook) extends DataLoader[Project] {
  def load(project: Project, context: Context): Workbook = workbook
}
