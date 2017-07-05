package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.loader.Loader
import com.geishatokyo.sqlgen.process.InputProc

/**
  * Created by takezoux2 on 2017/07/05.
  */
trait LoaderInput extends InputProc {

  def loader: Loader


}
