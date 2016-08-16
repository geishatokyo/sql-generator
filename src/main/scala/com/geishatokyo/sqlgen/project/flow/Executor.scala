package com.geishatokyo.sqlgen.project.flow

import com.geishatokyo.sqlgen.Project

/**
  * Created by takezoux2 on 2016/08/05.
  */
case class Executor(input: Input, processors: List[DataProcessor]) {


  def >>(output: Output) = {
    val data = input.read()

    val results = processors.reverse.
      foldLeft(data)((d,proc) => proc.process(d))

    output.output(results)
    this
  }


  def >>(proc: DataProcessor) = {
    new Executor(input,proc :: processors)
  }

}