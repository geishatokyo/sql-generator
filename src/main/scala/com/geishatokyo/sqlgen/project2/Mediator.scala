package com.geishatokyo.sqlgen.project2

import com.geishatokyo.sqlgen.sheet.Workbook
import com.geishatokyo.sqlgen.Context

/**
 * 
 * User: takeshita
 * DateTime: 13/07/12 2:03
 */
class Mediator(input : Input, project : Project) {

  private var resultCache : Option[List[(Context,Workbook)]] = None

  private def executeInternal : List[(Context,Workbook)] = {
    resultCache.getOrElse{
      resultCache = Some(input.read().map(wb => {
        wb._1 -> project(wb._2)
      }))
      resultCache.get
    }
  }

  def >>(output : Output) : Mediator = {
    executeInternal.foreach( p => {
      output.write(p._1,p._2)
    })
    this
  }

  def execute : List[Workbook] = {
    executeInternal.map(_._2)

  }


}
