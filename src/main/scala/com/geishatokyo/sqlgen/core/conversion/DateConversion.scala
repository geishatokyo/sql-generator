package com.geishatokyo.sqlgen.core.conversion

import java.time.{Instant, LocalDateTime, ZoneId, ZonedDateTime}
import java.time.format.DateTimeFormatter

import com.geishatokyo.sqlgen.SQLGenException
/**
  * Created by takezoux2 on 2017/05/27.
  */
trait DateConversion {

  def dateToDouble(d: ZonedDateTime): Double
  def dateToString(d: ZonedDateTime): String
  def stringToDate(s: String): ZonedDateTime
  def doubleToDate(d: Double): ZonedDateTime
}

trait UnixTimeBaseConversion extends DateConversion{
  override def dateToDouble(d: ZonedDateTime): Double = {
    d.toInstant.toEpochMilli
  }



  override def doubleToDate(d: Double): ZonedDateTime = {
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(d.toLong), ZoneId.systemDefault())
  }
}

trait VariousStringFormatConversion extends DateConversion{

  val formats = Array(
    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"),
    DateTimeFormatter.ISO_OFFSET_DATE_TIME,
    DateTimeFormatter.BASIC_ISO_DATE,
    DateTimeFormatter.ofPattern("yyyy/MM/dd"),
    DateTimeFormatter.ofPattern("yyyy-MM-dd")
  )


  override def dateToString(d: ZonedDateTime): String = {
    formats.head.format(LocalDateTime.ofInstant(d.toInstant, ZoneId.systemDefault()))
  }

  override def stringToDate(s: String): ZonedDateTime = {
    formats.view.map(f => {
      try{
        Some(ZonedDateTime.parse(s, f))
      }catch{
        case t: Throwable => None
      }
    }).find(_.isDefined).map(_.get).getOrElse{
      throw new SQLGenException(s"Wrong date format ${s}")
    }
  }
}