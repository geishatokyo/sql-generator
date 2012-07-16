package com.geishatokyo.sqlgen.external.python

import java.lang.ProcessBuilder
import com.geishatokyo.sqlgen.logger.Logger
import com.geishatokyo.sqlgen.external.ProcessSupport

/**
 * Support invoking pytnon script
 * User: takeshita
 * Create: 11/11/16 13:02
 *
 *
 */

object PythonWrapper {

  var pythonPath = "python"

}

class PythonWrapper(pythonFile: String) extends ProcessSupport {

  def version(): String = {

    val pb = new ProcessBuilder(PythonWrapper.pythonPath, "-V")
    pb.redirectErrorStream(true)
    val process = pb.start
    process.waitFor()
    val result = new String(loadStream(process.getInputStream))
    closeProcessStreams(process)
    result
    //invoke(List("-V"))
  }

  def runScript(args: String*): String = {
    invoke(pythonFile :: args.toList)
  }

  def invoke(args: List[String]): String = {
    val pb = new ProcessBuilder((PythonWrapper.pythonPath :: args): _*)
    pb.redirectErrorStream(true)
    val process = pb.start
    val result = new String(loadStream(process.getInputStream))
    closeProcessStreams(process)
    process.exitValue() match {
      case 0 => Logger.log("execute python script. exitCode=%s".format(0))
      case n: Int => {
        throw new RuntimeException("Python script error exitCode=%s result=%s".format(n, result))
      }
    }

    result
  }


}