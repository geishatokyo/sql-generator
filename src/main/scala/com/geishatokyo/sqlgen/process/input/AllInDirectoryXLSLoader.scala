package com.geishatokyo.sqlgen.process.input

import com.geishatokyo.sqlgen.{Context, DataLoader}
import com.geishatokyo.sqlgen.project.BaseProject
import com.geishatokyo.sqlgen.sheet.Workbook
import java.io.File
import com.geishatokyo.sqlgen.util.FileUtil

/**
 *
 * User: takeshita
 * Create: 12/07/19 0:20
 */

object AllInDirectoryXLSLoader{
  def apply(directory : String) = {
    new AllInDirectoryXLSLoader(new File(directory))
  }
}


class AllInDirectoryXLSLoader(searchDir : File) extends DataLoader[BaseProject] {
  def load(project: BaseProject, context: Context): Workbook = {
    val (dir,name,ext) = FileUtil.splitPathAndNameAndExt(searchDir.getAbsolutePath)

    context.workingDir = searchDir.getAbsolutePath
    val wbs = searchDir.listFiles().withFilter( f => {
      f.getAbsolutePath.endsWith(".xls")
    }).map(f => {
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

}
