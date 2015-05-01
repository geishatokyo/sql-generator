package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.sheet.ColumnType
import com.geishatokyo.sqlgen.setting.GTEDefaultProject
import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.process.input.SingleXLSLoader
import com.geishatokyo.sqlgen.process.output.SQLOutputProvider
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import com.geishatokyo.sqlgen.process.MapContext
import org.scalatest.{Matchers, FlatSpec}
import com.geishatokyo.sqlgen.process.input.InputHelpers._

/**
 *
 * User: takeshita
 * Create: 12/07/12 16:48
 */

class XLSProjectTest extends FlatSpec with Matchers {

  "BaseProject executor" should
    "apply project specification" in{

      val executor = new ExecutorUsage()

      val workbook = executor.execute(file("sample2.xls"))

      assert(workbook.getSheet("IgnoredSheet") == None)

      val sheet = workbook("Converted")
      assert(sheet.existColumn("id") == true)
      assert(sheet.existColumn("time") == true)
      assert(sheet.existColumn("language") == true)
      assert(sheet.existColumn("autoAdded") == true)
    }



}
class ExecutorUsage extends Executor[BaseProjectUsage]
  with EnsureProcessProvider
  with SQLOutputProvider{

  type ProjectType = BaseProjectUsage

  val context: Context = new MapContext

  val project = new BaseProjectUsage()

  protected def executor  = {
    ensureSettingProc then outputSqlProc()
  }

}

class BaseProjectUsage extends BaseProject{

  ignore sheet "IgnoredSheet"

  map sheetName("TestSheet" -> "Converted")


  onSheet("Converted"){
    ignore column "ignored"
    map columnName("言語" -> "language")

    ensure column "autoAdded" set "defaultV" when empty;
    ensure column "autoAdded" exists;
    ensure column "id" throws error whenNotExists;
    ensure column "language" convert( v => v match{
      case "日本語" => "Japanese"
      case "英語" => "English"
      case s => s
    })

  }

}
