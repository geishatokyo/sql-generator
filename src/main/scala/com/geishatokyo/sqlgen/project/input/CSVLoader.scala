package com.geishatokyo.sqlgen.project.input

import java.io.File

import com.geishatokyo.sqlgen.sheet.Sheet
import com.geishatokyo.sqlgen.util.FileUtil

/**
  * Created by takezoux2 on 2016/08/10.
  */
object CSVLoader {

  def load(f: File) : Sheet = {
    val (path,name,ext) = FileUtil.splitPathAndNameAndExt(f.getAbsolutePath)
    load(name,FileUtil.loadFileAsString(f))
  }

  def load(sheetName: String, csv: String) : Sheet = {
    val sheet = new Sheet(sheetName)
    val lines = csv.lines

    var setHeader = false

    lines.foreach(line => {
      if(line.startsWith("#")){
        val command = line.drop(1).trim
        if(command.startsWith("Name:")){
          sheet.name = command.drop("Name:".length).trim
        }
      }else{
        val splited = split(line)

        if(setHeader){
          sheet.addRow(splited.toList)
        }else{
          sheet.addColumns(splited :_*)
          setHeader = true
        }
      }
    })
    sheet


  }

  def split(line: String) = {
    line.split(",")
  }


}
