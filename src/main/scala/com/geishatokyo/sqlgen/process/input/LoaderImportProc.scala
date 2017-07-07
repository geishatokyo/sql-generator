package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.Loader
import com.geishatokyo.sqlgen.process.{Context, ImportProc}

/**
  * Created by takezoux2 on 2017/07/06.
  */
trait LoaderImportProc extends ImportProc {

  def loader: Loader
}
