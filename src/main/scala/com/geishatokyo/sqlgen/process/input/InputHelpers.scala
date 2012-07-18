package com.geishatokyo.sqlgen.process.input

/**
 *
 * User: takeshita
 * Create: 12/07/19 0:35
 */

object InputHelpers {

  def file( f : String) = {
    SingleXLSLoader.apply(f)
  }

  def inDir(dir : String) = {
    AllInDirectoryXLSLoader.apply(dir)
  }
}
