package com.geishatokyo.sqlgen.external

import java.io.{ByteArrayOutputStream, InputStreamReader, BufferedReader, InputStream}

/**
 *
 * User: takeshita
 * Create: 11/11/16 13:04
 */

trait ProcessSupport {

  def closeProcessStreams(process: Process) = {
    process.getOutputStream().close()
    process.getInputStream().close()
    process.getErrorStream().close()
  }

  def printStream(input: InputStream) = {
    val r = new BufferedReader(new InputStreamReader(input))

    var line: String = r.readLine()
    while (line != null) {
      println(line)
      line = r.readLine()
    }

  }

  def loadStream(input: InputStream): Array[Byte] = {

    val bao = new ByteArrayOutputStream(10000)
    var d = input.read()
    while (d >= 0) {
      bao.write(d.toByte)
      d = input.read()
    }
    bao.toByteArray
  }
}