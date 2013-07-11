package com.geishatokyo.sqlgen.sample

import org.specs2.mutable.Specification
import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.importall._
import com.geishatokyo.sqlgen.process.input.InputHelpers._
import com.geishatokyo.sqlgen.process.MapContext
import com.geishatokyo.sqlgen.setting.GTEDefaultProject

/**
 *
 * User: takeshita
 * Create: 12/07/12 18:32
 */

class PlainProjectTest extends Specification {

  "Executor" should{
    "load xls and save sql" in {
      val exe = new SimpleExecutor
      exe.execute(file("sample.xls"))

      ok
    }
  }

}

class SimpleExecutor extends Executor[BaseProject] with SQLOutputProvider{

  type ProjectType = BaseProject


  val context: Context = new MapContext

  val project = new GTEDefaultProject {
    onSheet("Test"){

    }
  }

  protected def executor  = {
    outputSqlProc()
  }

}
