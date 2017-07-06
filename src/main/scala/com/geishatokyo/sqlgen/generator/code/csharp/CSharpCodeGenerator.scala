package com.geishatokyo.sqlgen.generator.code.csharp

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.{Column, DataType, Row}
import com.geishatokyo.sqlgen.meta.{ColumnMeta, Metadata}

/**
  * Created by takezoux2 on 2017/07/06.
  */
class CSharpCodeGenerator {


  protected def getClassName(row: Row)(implicit metadata: Metadata) = {
    metadata.getSheetMeta(row.parent.name) match{
      case Some(metadata) => metadata.className
      case None => row.parent.name
    }
  }

  protected def getColumnMeta(c: Column)(implicit metadata: Metadata): ColumnMeta = {
    metadata.getSheetMeta(c.parent.name).flatMap(sm => {
      sm.getColumnMeta(c.header.name)
    }).getOrElse {
      throw SQLGenException.atSheet(c.parent,s"C# metadata for column:${c.name} not found")
    }
  }

  def createStatement(row: Row)(implicit metadata: Metadata): String = {
    val className = getClassName(row)

    val params = row.cells.map(c => {
      val meta = getColumnMeta(c.column)

      val csharpType = if(meta.className == Metadata.AutoClass) {
        c.dataType match{
          case DataType.Integer => "long"
          case DataType.Number => "double"
          case DataType.Bool => "boolean"
          case DataType.Date => "DateTime"
          case DataType.String => "string"
          case d => {
            throw SQLGenException.atCell(c, s"Not supported DataType:${d}")
          }
        }
      } else {
        meta.className
      }

      csharpType match {
        case "long" | "int" | "uint" | "byte" | "ulong" => c.asLong.toString
        case "double" | "float" => c.asDouble.toString
        case "string" => '"' + c.asString + '"'
        case "boolean" => c.asBool.toString
        case "DateTime" => {
          val d = c.asDate
          s"new DateTime(${d.getYear}, ${d.getMonth}, ${d.getDayOfMonth}, ${d.getHour}, ${d.getMinute}, ${d.getSecond})"
        }
        case _ => {
          throw SQLGenException.atCell(c, s"Not supported C#Type:${csharpType}")
        }
      }
    })

    s"new ${className}(${params.mkString(",")})"

  }


}
