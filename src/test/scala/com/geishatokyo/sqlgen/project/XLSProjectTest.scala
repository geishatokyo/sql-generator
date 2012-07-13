package com.geishatokyo.sqlgen.project

import com.geishatokyo.sqlgen.sheet.ColumnType
import com.geishatokyo.sqlgen.setting.GTEDefaultProject
import com.geishatokyo.sqlgen.{Context, Executor}
import com.geishatokyo.sqlgen.process.input.XLSFileInput
import com.geishatokyo.sqlgen.process.output.SQLOutputProvider
import com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
import com.geishatokyo.sqlgen.process.MapContext
import org.specs2.mutable.SpecificationWithJUnit

/**
 *
 * User: takeshita
 * Create: 12/07/12 16:48
 */

class XLSProjectTest extends SpecificationWithJUnit {

  "BaseProject" should {
    "works" in{

      val executor = new ExecutorUsage()

      val workbook = executor.execute("sample2.xls")

      workbook.getSheet("IgnoredSheet") must beNone

      val sheet = workbook("Converted")
      sheet.existColumn("id") must_== true
      sheet.existColumn("time") must_== true
      sheet.existColumn("language") must_== true
      sheet.existColumn("autoAdded") must_== true
    }
  }


}
class ExecutorUsage extends Executor[BaseProjectUsage]
  with XLSFileInput
  with EnsureProcessProvider
  with SQLOutputProvider{

  type ProjectType = BaseProjectUsage

  val context: Context = new MapContext

  val project = new BaseProjectUsage()

  protected def executor  = {
    ensureSettingProc then outputSqlProc
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
