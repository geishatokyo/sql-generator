package com.geishatokyo.sqlgen.setting

/**
  * sql-genのグローバル設定取得
  * 今のところ、envからのみ取ってくる
  * Created by takezoux2 on 2017/11/30.
  */
object SettingLoader {

  def getString(key: String): Option[String] = {
    Option(System.getenv(key))
  }

}
