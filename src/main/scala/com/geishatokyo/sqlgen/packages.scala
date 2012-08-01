package com.geishatokyo.sqlgen

package object importall{

  type BaseProject = com.geishatokyo.sqlgen.project.BaseProject
  type CrossWorkbookProject = com.geishatokyo.sqlgen.project.CrossWorkbookProject
  type MergeSplitProject = com.geishatokyo.sqlgen.project.MergeSplitProject
  type TimeHelper = com.geishatokyo.sqlgen.project.TimeHelper
  type ValidateProject = com.geishatokyo.sqlgen.project.ValidateProject

  type SQLOutputProvider = com.geishatokyo.sqlgen.process.output.SQLOutputProvider
  type XLSOutputProvider = com.geishatokyo.sqlgen.process.output.XLSOutputProvider
  type EnsureProcessProvider = com.geishatokyo.sqlgen.process.ensure.EnsureProcessProvider
  type MergeSplitProcessProvider = com.geishatokyo.sqlgen.process.merge.MergeSplitProcessProvider
  type I18NProcessProvider = com.geishatokyo.sqlgen.process.merge.I18NProcessProvider
  type WorkbookMergeProcessProvider = com.geishatokyo.sqlgen.process.merge.WorkbookMergeProcessProvider
  type ValidateProcessProvider = com.geishatokyo.sqlgen.process.validate.ValidateProcessProvider

}