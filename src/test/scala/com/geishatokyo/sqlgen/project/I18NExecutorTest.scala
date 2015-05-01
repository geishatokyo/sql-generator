package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.process.merge.I18NProcessProvider
import com.geishatokyo.sqlgen.{Executor, Project, Context}
import com.geishatokyo.sqlgen.process.output.SQLOutputProvider
import com.geishatokyo.sqlgen.process.{Proc, MapContext}
import com.geishatokyo.sqlgen.process.input.{InputHelpers, SingleXLSLoader}
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import org.scalatest.{Matchers, FlatSpec}
import InputHelpers._

/**
 *
 * User: takeshita
 * Create: 12/07/14 2:31
 */

class I18NExecutorTest extends FlatSpec with Matchers {

  "i18nExecutor" should
    "merge i18n files" in{
      val executor = new I18NExecutorSample()
      val wb = executor.execute( file("i18n/i18n.xls"))

      println(wb)
    }


}

class I18NExecutorSample extends Executor[BaseProject]
with EnsureProcessProvider
with I18NProcessProvider
with SQLOutputProvider{

  type ProjectType = BaseProject


  val context: Context = new MapContext

  val project = new SampleProject()

  protected def executor: Proc = ensureSettingProc then i18nProc(
      ensureSettingProc,
      outputSqlProc("i18n")
      )

  class SampleProject extends BaseProject{

  }

}
