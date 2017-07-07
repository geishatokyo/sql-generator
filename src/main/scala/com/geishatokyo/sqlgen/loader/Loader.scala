package com.geishatokyo.sqlgen.loader

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}

import com.geishatokyo.sqlgen.SQLGenException
import com.geishatokyo.sqlgen.core.Workbook
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.process.WorkbookMerger


/**
  * Created by takezoux2 on 2017/07/05.
  */
trait Loader {
  def load(path: String): Workbook = load(new File(path))
  def load(file: File): Workbook = {
    Logger.log("load file " + file.getAbsolutePath)
    val name = if(file.getName.indexOf(".") >= 0) {
      file.getName.substring(0,file.getName.indexOf("."))
    } else {
      file.getName
    }
    load(name, new FileInputStream(file))
  }
  def load(name: String, input: InputStream): Workbook

  def encoding = "utf-8"

  def loadFromString(name: String, str: String) = {
    load(name, new ByteArrayInputStream(str.getBytes(encoding)))
  }
  def loadMulti(path: Iterator[String]): Workbook = {
    if(path.hasNext){
      path.map(path => load(path)).reduceLeft(WorkbookMerger.merge _)
    } else {
      throw SQLGenException("No files are passed")
    }
  }

  def loadMultiFile(path: Iterator[File]): Workbook = {
    if(path.hasNext){
      path.map(path => load(path)).reduceLeft(WorkbookMerger.merge _)
    } else {
      throw SQLGenException("No files are passed")
    }
  }

  def loadMultiInput(name: String, path: Iterator[InputStream]): Workbook = {
    if(path.hasNext){
      path.map(path => load(name, path)).reduceLeft(WorkbookMerger.merge _)
    } else {
      throw SQLGenException("No files are passed")
    }
  }
}
