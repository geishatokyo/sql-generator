package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.process.input.{InputHelpers, SingleXLSLoader}
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import com.geishatokyo.sqlgen.process.output.{XLSOutputProvider, SQLOutputProvider}
import com.geishatokyo.sqlgen.process.merge.MergeSplitProcessProvider
import com.geishatokyo.sqlgen.process.{MapContext, Proc}
import org.specs2.mutable.Specification
import InputHelpers._

/**
 *
 * User: takeshita
 * Create: 12/07/13 11:47
 */

class MergeSplitProjectTest extends Specification {

  "MergeSplit executor" should{

    "apply project specification" in{

      val e = new MergeSplitExecutorSample
      val wb = e.execute(file("MergeSplitTest.xls"))

      wb("User").column("name").cells.map(_.value) must_== List("test","test2","あああ")
      wb("User").column("familyname").cells.map(_.value) must_== List("tanaka","yamada","satou")
      wb("User").column("gender").cells.map(_.value) must_== List("male","female","female")

    }
  }

}

class MergeSplitExecutorSample extends Executor[MergeSplitProjectSample]
with EnsureProcessProvider
with MergeSplitProcessProvider
with SQLOutputProvider
with XLSOutputProvider{

  val project = new MergeSplitProjectSample

  val context: Context = new MapContext

  type ProjectType = MergeSplitProjectSample

  protected def executor: Proc = {
    ensureSettingProc then
    mergeAndSplitProc then
    outputSqlProc().skipOnError then
    outputXlsProc().skipOnError
  }
}

class MergeSplitProjectSample extends BaseProject with MergeSplitProject{



  merge sheet "User" from(
    at("Sheet1"){
      column("name")
      column("gender")
    } ,
    at("Sheet2"){
      column("familyName")
    }
    )

  merge sheet "User" select ("name" as "gender") from "Sheet3" where "gender" is "id"

}
