package com.geishatokyo.sqlgen.project

/**
 *
 * User: takeshita
 * Create: 12/09/03 17:37
 */
class ReferenceProjectTest {

}


class ReferenceProjectSample extends BaseProject with ReferenceProject{

  onSheet("Sheet1"){
    set.column("name").from("Sheet2").
      where( (myRow,refRow) => myRow("id") == refRow("id")).
      value(row => row("name").asString).
      whenEmpty
  }

}