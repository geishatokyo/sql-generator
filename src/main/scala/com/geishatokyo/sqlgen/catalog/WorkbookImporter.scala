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

    val passed = inputDatas.map(_.workbook)

    inputDatas.foreach(id => {
      id.context.references = (id.context.references ++ workbooks ++ passed).filter(w => {
        w != id.workbook
      })
    })

    inputDatas

  }
}
