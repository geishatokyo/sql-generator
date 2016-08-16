package com.geishatokyo.sqlgen.setting

import com.geishatokyo.sqlgen.Project
import com.geishatokyo.sqlgen.sheet.ColumnType

/**
 *
 * User: takeshita
 * Create: 12/07/12 21:12
 */

trait GTEDefaultProject extends Project {

  onSheet("""sheet\d*""".r){{
    ignore()
  }}

  onAllSheet{
    column("名前").name = "name"
    column("内部名").name = "innerName"
  }

  onAllSheet{
    columns.foreach(c => {
      val t = c.columnName match{
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
      }
      c.header.columnType = t
    })
  }

}
