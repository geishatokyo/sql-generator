package com.geishatokyo.sqlgen.sample

import org.scalatest.{Matchers, FlatSpec}
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

class PlainProjectTest extends FlatSpec with Matchers {

  "Executor" should
    "load xls and save sql" in {
      val exe = new SimpleExecutor
      exe.execute(file("sample.xls"))
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
