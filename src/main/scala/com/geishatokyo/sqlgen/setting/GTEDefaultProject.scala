package com.geishatokyo.sqlgen.setting

import com.geishatokyo.sqlgen.Project

/**
 *
 * User: takeshita
 * Create: 12/07/12 21:12
 */

trait GTEDefaultProject extends Project {

  onSheet("""sheet\d*""".r){{
    ignore()
  }}

  onAllSheet{
    column("名前").name = "name"
    column("内部名").name = "innerName"
  }

}
