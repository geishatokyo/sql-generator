package process

import com.geishatokyo.sqlgen.sheet.Workbook
import java.io._
import com.geishatokyo.sqlgen.util.{NoneFile, SomeFile, FileUtil}
import com.geishatokyo.sqlgen.Project
import com.geishatokyo.sqlgen.process.ProcessProvider
import java.util.UUID

/**
 *
 * User: takeshita
 * Create: 12/07/12 17:24
 */

trait Input extends ProcessProvider {


  def load( filename : String) : Workbook = {
    val path = FileUtil.findFile(filename)
    logger.log("Load file:" + path)
    val (dir,name,ext) = FileUtil.splitPathAndNameAndExt(path)
    context.workingDir = dir
    context.name = name
    val stream = new FileInputStream(new File(path))
    try{
      _load(stream)
    }finally{
      stream.close()
    }
  }

  def loadFromString( v : String) : Workbook = {
    load(new ByteArrayInputStream(v.getBytes("utf-8")))
  }

  def loadFromBytes( b : Array[Byte]) = {
    load(new ByteArrayInputStream(b))
  }

  def load( input : InputStream) : Workbook = {
    context.workingDir = new File(".").getAbsolutePath
    context.name = UUID.randomUUID().toString
    _load(input)
  }

  protected def _load( input : InputStream) : Workbook

}
