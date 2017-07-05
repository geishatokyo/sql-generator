package com.geishatokyo.sqlgen.catalog

import java.io.File

import com.geishatokyo.sqlgen.project.flow.{InputData, DataProcessor}
import com.geishatokyo.sqlgen.project.input.FileInput

/**
  * 依存するワークブックの追加を行う
  * Created by takezoux2 on 2016/08/10.
  */
class WorkbookImporter(files: File*) extends DataProcessor{


  override def process(inputDatas: List[InputData]): List[InputData] = {
    val workbooks = new FileInput(files:_*).read().map(_.workbook)

    if(workbooks.size > 0) {
      //依存しているワークブックは一つにまとめてしまう
      val refs =  List(workbooks.reduce((w1,w2) => {
        w1.merge(w2)
      }))
      inputDatas.foreach(id => {
        //id.context.references = refs
      })
    }

    inputDatas

  }
}
