package com.geishatokyo.sqlgen.sample

import org.specs2.mutable.SpecificationWithJUnit
import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.process.input.XLSFileInput
import com.geishatokyo.sqlgen.process.output.SQLOutputProvider
import com.geishatokyo.sqlgen.process.MapContext
import com.geishatokyo.sqlgen.setting.GTEDefaultProject

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:32
 */

class PlainProjectTest extends SpecificationWithJUnit {

  "Executor" should{
    "load xls and save sql" in {
      val exe = new SimpleExecutor
      exe.execute("sample.xls")

      ok
    }
  }

}

class SimpleExecutor extends Executor[BaseProject] with XLSFileInput with SQLOutputProvider{

  type ProjectType = BaseProject


  val context: Context = new MapContext

  val project = new GTEDefaultProject {
    onSheet("Test"){

    }
  }

  protected def executor  = {
    outputSqlProc
  }

}
