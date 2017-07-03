package com.geishatokyo.sqlgen.core

import com.geishatokyo.sqlgen.core.conversion.{DateConversion, DefaultDateConversion}

/**
  * Created by takezoux2 on 2017/06/11.
  */
object Global {


  var dateConversion: DateConversion = DefaultDateConversion
}
