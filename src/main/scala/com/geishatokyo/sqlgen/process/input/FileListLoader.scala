package com.geishatokyo.sqlgen.process.input

import java.io.File
import com.geishatokyo.sqlgen.{Context, DataLoader}
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/09/03 13:07
 */
class FileListLoader(files : List[File]) extends DataLoader[BaseProject] {
  def load(project: BaseProject, context: Context): Workbook = {

    val wbs = files.map( f => {
      val loader = SingleXLSLoader(f)
      loader.load(project,context)
    }).toList

    wbs match{
      case h :: left => left.foldLeft(h)( (base,merge) => mergeSheets(base,merge))
      case Nil => new Workbook()
    }
  }
  private def mergeSheets(base : Workbook, merge : Workbook) = {
    merge.foreachSheet(sheet => base.addSheet(sheet))
    base
  }

  def filter( filtr : String => Boolean) = {
    new FileListLoader( files.filter(f => filtr(f.getAbsolutePath)))
  }

}

object FileListLoader{

  def apply(dir : String, regex : String) = {
    val files = FileUtil.findFilesWithRegex(new File(dir),regex.r)
    new FileListLoader(files)
  }

}
