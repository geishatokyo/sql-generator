package com.geishatokyo.sqlgen.generator.code.csharp

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.{Column, DataType, Row}
import com.geishatokyo.sqlgen.meta.{ColumnMeta, Metadata}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class CSharpCodeGenerator(withLabel: Boolean = true) {


  def createStatement(row: Row): String = {
    val className = row.parent.name

    val params = row.cells.map( c => {
      val csharpType = c.header.columnType match {
        case None | Some(Metadata.AutoClass) => {
          c.dataType match {
            case DataType.Integer => "long"
            case DataType.Number => "double"
            case DataType.Bool => "bool"
            case DataType.Date => "DateTime"
            case DataType.String => "string"
            case d => {
              throw SQLGenException.atCell(c, s"Not supported DataType:${d}")
            }
          }
        }
        case Some(t) => t
      }


      val v = csharpType match {
        case "long" | "int" | "uint" | "byte" | "ulong" => c.asLong.toString
        case "double" | "float" => c.asDouble.toString
        case "string" => escape(c.asString)
        case "bool" => c.asBool.toString
        case "DateTime" => {
          val d = c.asDate
          s"new DateTime(${d.getYear}, ${d.getMonth().getValue}, ${d.getDayOfMonth}, ${d.getHour}, ${d.getMinute}, ${d.getSecond})"
        }
        case _ => {
          throw SQLGenException.atCell(c, s"Not supported C#Type:${csharpType}")
        }
      }
      if(withLabel) {
        c.header.name + ":" + v
      }else {
        v
      }
    })

    s"new ${className}(${params.mkString(",")})"

  }


  def escape(str: String) = {
    if(str.contains("\n")) {
      "@\"" + str.replace("\"","\"\"") + "\""
    } else {
      '"' + str.replace("\"","\\\"") + '"'
    }
  }

}
