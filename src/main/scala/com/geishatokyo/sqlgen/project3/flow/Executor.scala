package com.geishatokyo.sqlgen.project3.flow

import com.geishatokyo.sqlgen.project3.Project

/**
  * Created by takezoux2 on 2016/08/05.
  */
case class Executor(input: Input, project: Project) {


  def >>(output: Output) = {
    val t = input.read()
    output.output(t._1,t._2)
    this
  }


  def >>(project: Project) = {
    new Executor(input,this.project ++ project)
  }

}