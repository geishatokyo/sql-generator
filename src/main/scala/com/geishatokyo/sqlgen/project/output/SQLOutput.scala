package com.geishatokyo.sqlgen.project.output

import com.geishatokyo.sqlgen.Context
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.project.flow.{Output}
import com.geishatokyo.sqlgen.sheet.{Sheet, Workbook}
import com.geishatokyo.sqlgen.sheet.convert.{SQLiteConverter, MySQLConverter, SQLConverter}
import com.geishatokyo.sqlgen.util.FileUtil

/**
 * Created by takezoux2 on 15/05/05.
 */
class SQLOutput(converter : SQLConverter) extends Output{

  var path : String = ""

  def output(context: Context, w: Workbook): Unit = {

    writeSql(context,w,"insert",converter.toInsertSQL _)
    writeSql(context,w,"update",s => {
      converter.toUpdateSQL(s,s.ids.map(_.name))
    })
    writeSql(context,w,"delete",s => {
      converter.toDeleteSQL(s,s.ids.map(_.name))
    })
    writeSql(context,w,"replace",s => {
      converter.toReplaceSQL(s,s.ids.map(_.name))
    })

  }

  private def writeSql(context: Context, w: Workbook,action : String, convert : Sheet => String) {
    val sqls = w.sheets.filter( !_.ignore).map(convert)
    if (sqls.size > 0){

      val name = if(path.contains('.')) path
      else FileUtil.joinPath(path,s"${action}_${w.name}.sql")
      val filename = FileUtil.joinPath(context.workingDir,name)
      FileUtil.saveTo(filename,sqls)



      /*logger.log("Save " + action + " sql to %s".format(path))
      FileUtil.saveTo(path,sqls)*/
    }
  }
}


object SQLOutput{

  def toFile(filenameBase : String)(context : Context,action : String,sqls : List[String]) = {

    val (dir,fn,ex) = FileUtil.splitPathAndNameAndExt(filenameBase)
    val path = FileUtil.joinPath(
      FileUtil.joinPath(context.workingDir,dir),
      action + "_" + fn + ".sql")
    Logger.log("Save sql to " + path)
    FileUtil.saveTo(path,sqls)
  }

  def toConsole(context : Context,path : String,sqls : List[String]) = {
    sqls.foreach(println(_))
  }

  def toLogger(context : Context,path : String,sqls : List[String]) = {
    sqls.foreach(Logger.log(_))
  }

  def mysql() = {
    new SQLOutput(new MySQLConverter())
  }

  def sqlite() = {
    new SQLOutput(new SQLiteConverter())
  }

}
