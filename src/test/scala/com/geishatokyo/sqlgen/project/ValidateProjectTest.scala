package com.geishatokyo.sqlgen.project

/**
 *
 * User: takeshita
 * Create: 12/07/18 12:41
 */

class ValidateProjectTest {

}

class ValidateProjectSample extends BaseProject with ValidateProject{

  import ValidateProject._

  onSheet("Sheet1"){
    validate eachRows(row => row("a") == row("b"))
    validate column("c") is validXml;
    validate column("d") is notEmpty;
  }



}

