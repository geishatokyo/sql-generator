package com.geishatokyo.sqlgen.util

import java.util.{Calendar, Date}
import java.util.concurrent.TimeUnit

/**
 *
 * User: takeshita
 * DateTime: 13/10/17 0:20
 */
object TimeUtil {

  val ExcelTimePoint = {
    val c = Calendar.getInstance()
    c.set(1900,0,-1,0,0,0)
    c.getTime
  }

  val OneDay = TimeUnit.DAYS.toMillis(1)

  def excelTimeToJavaDate( daysFrom1900 : Double) : Date = {

    new Date( (ExcelTimePoint.getTime + daysFrom1900 * OneDay).toLong)

  }

  def javaDateToExcelTime( date : Date) : Double = {
    (date.getTime - ExcelTimePoint.getTime).toDouble / OneDay
  }

}
