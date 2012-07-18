package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.process.merge.I18NProcessProvider
import com.geishatokyo.sqlgen.{Executor, Project, Context}
import com.geishatokyo.sqlgen.process.output.SQLOutputProvider
import com.geishatokyo.sqlgen.process.{Proc, MapContext}
import com.geishatokyo.sqlgen.process.input.{InputHelpers, SingleXLSLoader}
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import org.specs2.mutable.SpecificationWithJUnit
import InputHelpers._

/**
 *
 * User: takeshita
 * Create: 12/07/14 2:31
 */

class I18NExecutorTest extends SpecificationWithJUnit {

  "i18nExecutor" should{

    "merge i18n files" in{
      val executor = new I18NExecutorSample()
      val wb = executor.execute( file("i18n/i18n.xls"))

      println(wb)
      ok
    }
  }

}

class I18NExecutorSample extends Executor[BaseProject]
with EnsureProcessProvider
with I18NProcessProvider
with SQLOutputProvider{
  type ProjectType = BaseProject
  val project = new SampleProject()

  protected def executor: Proc = ensureSettingProc then i18nProc(
    ensureSettingProc,
    outputSqlProc("i18n")
  )

  val context: Context = new MapContext

  class SampleProject extends BaseProject{

  }

}
