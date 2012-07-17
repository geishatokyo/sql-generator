package com.geishatokyo.sqlgen.project

import java.util.{Date, Calendar}

/**
 *
 * User: takeshita
 * Create: 12/07/17 23:58
 */

trait TimeHelper {

  def time(year : Int, month : Int, dayOfMonth : Int,hour : Int = 0, minute : Int = 0): String = {
    val cal = Calendar.getInstance()
    cal.set(year,month - 1,dayOfMonth,hour,minute)
    cal.getTime.getTime.toString
  }

  def modifyCalendar(calFunc : Calendar => Calendar)(v : String): String = {

    val d = new Date(v.toLong)
    val cal = Calendar.getInstance()
    calFunc(cal).getTime.getTime.toString
  }

  def addHours( hour : Int)( v : String) : String = {
    val d = new Date(v.toLong)
    val cal = Calendar.getInstance()
    cal.setTime(d)
    cal.add(Calendar.HOUR,hour)
    cal.getTime.getTime.toString
  }

  def addMinutes( minutes : Int)( v : String): String = {
    val d = new Date(v.toLong)
    val cal = Calendar.getInstance()
    cal.setTime(d)
    cal.add(Calendar.MINUTE,minutes)
    cal.getTime.getTime.toString
  }

  def addDays( days : Int)( v : String): String = {
    val d = new Date(v.toLong)
    val cal = Calendar.getInstance()
    cal.setTime(d)
    cal.add(Calendar.DAY_OF_YEAR,days)
    cal.getTime.getTime.toString
  }

  def now = {
    new Date().getTime.toString
  }

}
