package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook
import input.XLSLoader
import java.io.File
import com.geishatokyo.sqlgen.util.FileUtil
import com.geishatokyo.sqlgen.process.input.SingleXLSLoader
import com.geishatokyo.sqlgen.Context

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:03
 */
trait Input {

  def >>(project : Project) : Mediator = {
    new Mediator(this,project)
  }

  def read() : List[(Context,Workbook)]

  def +(input : Input) : Input = {
    input match {
      case list : InputList => {
        InputList(this :: list.inputs)
      }
      case _ => {
        InputList(this :: input :: Nil)
      }
    }
  }

}

case class InputList(inputs : List[Input]) extends Input {
  def read() = {
    inputs.flatMap( input => {
      input.read()
    })
  }

  override def +(input : Input) : Input = {
    input match {
      case list : InputList => {
        InputList(inputs ::: list.inputs)
      }
      case _ => {
        InputList(inputs :+ input)
      }
    }
  }

}

