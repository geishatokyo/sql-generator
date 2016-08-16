package com.geishatokyo.sqlgen.catalog

import com.geishatokyo.sqlgen.project.flow.{InputData, DataProcessor}

/**
  * 複数のワークブックをまとめる
  * contextは先頭のワークブックのものが使われる
  * Created by takezoux2 on 2016/08/10.
  */
class WorkbooKMerger extends DataProcessor {
  override def process(inputDatas: List[InputData]): List[InputData] = {
    if(inputDatas.size == 0) Nil
    else{
      List(inputDatas.reduce[InputData]((l,r) => {
        InputData(l.context,l.workbook.merge(r.workbook))
      }))
    }
  }
}
