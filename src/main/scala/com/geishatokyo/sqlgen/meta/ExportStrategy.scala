package com.geishatokyo.sqlgen.meta

/**
  * Created by takezoux2 on 2018/03/03.
  */
sealed trait ExportStrategy {

}


object ExportStrategy {

  case object Export extends ExportStrategy
  case object DontExport extends ExportStrategy
  case object ThrowException extends ExportStrategy

}
