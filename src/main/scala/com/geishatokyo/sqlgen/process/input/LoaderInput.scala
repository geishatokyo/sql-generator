package com.geishatokyo.sqlgen.process.input

import java.io.File

import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.loader.{CSVLoader, Loader}
import com.geishatokyo.sqlgen.process.{Context, EmptyProc, InputProc, Proc}

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait LoaderInput extends InputProc {

  def loader: Loader

}


