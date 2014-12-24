package com.geishatokyo.sqlgen.process.input

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.sheet.load.hssf.{NameMapper, ColumnTypeGuesser, XLSSheetLoader}
import com.geishatokyo.sqlgen.{Context, DataLoader}
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:55
 */

object SingleXLSLoader{

  def apply( filename : String) : SingleXLSLoader  = {
    apply(new File( FileUtil.findFile(filename)))
  }

  def apply(f : File) : SingleXLSLoader = {
    val (dir,fn,ext) = FileUtil.splitPathAndNameAndExt(f.getAbsolutePath)
    new SingleXLSLoader(dir,fn,() => {
      new FileInputStream(f)
    })
  }

  def fromBinary( currentDir : String,name : String, data : Array[Byte]) = {
    new SingleXLSLoader(currentDir,name,() => {
      new ByteArrayInputStream(data)
    })
  }


}

class SingleXLSLoader(workingDir : String,name : String,getInputStream : () => InputStream) extends DataLoader[BaseProject]{


  def load(project: BaseProject, context: Context): Workbook = {
    context.workingDir = workingDir
    val loader = new XLSSheetLoader(
      new NameMapperWrapper(project)
    )
    val stream = getInputStream()
    try{
      val wb = loader.load(stream)
      wb.name = name
      context.name = name
      wb
    }finally{
      stream.close()
    }
  }

  class ColumnTypeGuesserWrapper(project : BaseProject) extends ColumnTypeGuesser{
    def guesserFor(sheetName: String) = {
      project(sheetName).columnTypeGuesser
    }

    def isIgnoreColumn_?(sheetName: String) : String => Boolean = {
      project(sheetName).ignoreColumns
    }

    def isIdColumn_?(sheetName: String): (String) => Boolean = {
      project(sheetName).idColumnGuesser
    }
  }

  class NameMapperWrapper(project : BaseProject) extends NameMapper{
    def isIgnoreSheet_?(sheetName: String): Boolean = {
      project.ignoreSheetNames(sheetName)
    }

    def mapSheetName(sheetName: String): String = {
      project.sheetNameMaps(sheetName)
    }

    def columnNameMapperFor(sheetName: String): (String) => String = {
      project(sheetName).columnNameMaps
    }
  }

}
