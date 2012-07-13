package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.process.Input
import java.io.InputStream
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.sheet.load.hssf.{NameMapper, ColumnTypeGuesser, XLSSheetLoader}

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:55
 */

trait XLSFileInput extends Input{

  type ProjectType <: BaseProject

  class ColumnTypeGuesserWrapper extends ColumnTypeGuesser{
    def guesserFor(sheetName: String) = {
      project(sheetName).columnTypeGuesser
    }

    def isIgnoreColumn_?(sheetName: String) : String => Boolean = {
      project(sheetName).ignoreColumns
    }

    def isIdColumn_?(sheetName: String): (String) => Boolean = {
      project(sheetName).idColumnGuesser
    }
  }

  class NameMapperWrapper extends NameMapper{
    def isIgnoreSheet_?(sheetName: String): Boolean = {
      project.ignoreSheetNames(sheetName)
    }

    def mapSheetName(sheetName: String): String = {
      project.sheetNameMaps(sheetName)
    }

    def columnNameMapperFor(sheetName: String): (String) => String = {
      project(sheetName).columnNameMaps
    }
  }

  val sheetLoader = new XLSSheetLoader(
    new NameMapperWrapper(),
    new ColumnTypeGuesserWrapper()
  )

  def _load(input: InputStream): Workbook = {
    sheetLoader.load(input)
  }
}
