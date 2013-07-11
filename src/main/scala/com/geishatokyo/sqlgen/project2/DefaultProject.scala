package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.ColumnType
import com.geishatokyo.sqlgen.project.TimeHelper

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 1:52
 */
trait DefaultProject extends Project with TimeHelper{

  guessColumnInformation()

  def guessColumnInformation() = {

    guessId( columnName => {
      columnName.toLowerCase == "id"
    })

    guessColumnType( {
      case v if v.endsWith("id") => ColumnType.Integer
      case v if v.endsWith("name") => ColumnType.String
      case v if v.startsWith("thumb") => ColumnType.String
      case v if v.endsWith("key") => ColumnType.String
      case v if v.startsWith("time") => ColumnType.Date
      case v if v.endsWith("date") => ColumnType.Date
      case v if v.endsWith("time") => ColumnType.Date
      case "explanation" => ColumnType.String
      case "description" => ColumnType.String
      case "desc" => ColumnType.String
      case "summary" => ColumnType.String
      case "created" => ColumnType.Date
      case "updated" => ColumnType.Date
      case "expire" => ColumnType.Date
      case v if v.contains("begin") => ColumnType.Date
      case v if v.contains("start") => ColumnType.Date
      case v if v.contains("end") => ColumnType.Date
      case v if v.contains("finish") => ColumnType.Date
      case v if v.contains("text") => ColumnType.String
    })
  }

}
