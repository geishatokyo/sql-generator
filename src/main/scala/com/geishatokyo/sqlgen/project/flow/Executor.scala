package com.geishatokyo.sqlgen.project.flow

import com.geishatokyo.sqlgen.Project

/**
  * Created by takezoux2 on 2016/08/05.
  */
case class Executor(input: Input, project: Project) {


  def >>(output: Output) = {
    val t = input.read()
    val wb = project(t._2)
    output.output(t._1,wb)
    this
  }


  def >>(project: Project) = {
    new Executor(input,this.project ++ project)
  }

}