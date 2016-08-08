package com.geishatokyo.sqlgen.sheet

import org.scalatest.{Matchers, FlatSpec}

/**
 * Created by takezoux2 on 15/05/05.
 */
class CellTest extends FlatSpec with Matchers {

  "String cell" should "equal to" in {
    val parent = new Sheet("TestSheet")
    val c = new Cell(parent,"112")

    // Normal equal
    assert(c == "112")
    assert(c != 112)
    assert(c != 112.0)
    assert(c == new Cell(parent,"112"))
    assert(c != 2321)
    // About equal
    assert(c ~== "112")
    assert(c ~== 112)
    assert(c !~== 112.0)
    assert(c ~== new Cell(parent,"112"))
    assert(c !~== 2321)
  }

  "Int cell" should "equal to" in {

    val parent = new Sheet("TestSheet")
    val c = new Cell(parent,234)

    assert(c == 234)
    assert(c == 234.0)
    assert(c != "234")
    assert(c == new Cell(parent,234))
    assert(c != 221334)


    assert(c ~== 234)
    assert(c ~== 234.0)
    assert(c ~== "234")
    assert(c ~== new Cell(parent,234))
    assert(c !~== 221334)
  }

}
