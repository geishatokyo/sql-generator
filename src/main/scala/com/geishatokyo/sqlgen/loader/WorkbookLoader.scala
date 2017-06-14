package com.geishatokyo.sqlgen.loader

import com.geishatokyo.sqlgen.core.Workbook

/**
  * Created by takezoux2 on 2017/06/14.
  */
trait WorkbookLoader {

  def load() : Workbook

}
