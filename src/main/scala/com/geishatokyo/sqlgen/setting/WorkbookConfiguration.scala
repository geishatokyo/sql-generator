package com.geishatokyo.sqlgen.setting

import java.util.Comparator


/**
  * Workbook単位のコンフィグ
  * Created by takezoux2 on 2017/12/01.
  */
trait WorkbookConfiguration {

  /**
    * カラム名など等比較に使用
    * デフォルトは大文字、小文字を区別しない
    * @return
    */
  def stringComparator: Comparator[String]

}

/**
  * Configの内容を利用して行う処理の便利メソッドを提供
  */
trait WorkbookConfSupport {
  def config: WorkbookConfiguration

  protected def eqStr(s1: String, s2: String) = {
    config.stringComparator.compare(s1,s2) == 0
  }

  /**
    * filterメソッドなどに渡す場合の便利メソッド
    * @param s1
    * @return
    */
  protected def eqStrWith(s1: String): String => Boolean = (s2: String) => {
    eqStr(s1,s2)
  }

}

class EqWrappedStr(s: String, comp: Comparator[String]) {
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case s2: String => comp.compare(s,s2 ) == 0
      case _ => false
    }
  }
}


object DefaultWorkbookConfig extends WorkbookConfiguration {
  override def stringComparator: Comparator[String] = {
    CaseInsensitiveStringComp
  }
}

object CaseInsensitiveStringComp extends Comparator[String] {
  override def compare(o1: String, o2: String): Int = {
    if(o1 == null && o2 == null) 0
    else if(o1 != null && o2 != null) {
      o1.toUpperCase.compare(o2.toUpperCase)
    } else {
      -1
    }
  }
}

object CaseSensitiveStringComp extends Comparator[String] {
  override def compare(o1: String, o2: String): Int = {
    if(o1 == null && o2 == null) 0
    else if(o1 != null && o2 != null) {
      o1.compare(o2)
    } else {
      -1
    }
  }
}