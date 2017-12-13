# SQL Generator

This library supports generating SQL from Excel sheets (.xls), CSV files and other formats.

# Getting started



# Data sheet formats

## Excel (.xls or .xlsx) files

Each sheet corresponds to a database table.

The sheet name should be the desired table name.<br />
Within the sheet, the first row must be composed of the column names.<br />
Later rows are interpreted as records.

# Define project

You write your conversion rules in your project class.
Sample code is below.

    import com.geishatokyo.sqlgen._
    object YourProject extends DefaultProject{

      def main(args : Array[String]) {
      
        fromFile("hoge.xls","fuga.csv","aaa.xlsx") >>
          YourProject >>
          xls.toDir("output/xls") >> 
          mysql.toDir("output/sql") <>
          csv.toConsole execute()
         
      }

      addSheet("NewSheet");
      
      onSheet("SheetToIgnore"){
        sheet.ignore = true
      }

      onSheet("Sheet1"){
        sheet.rows.foreach(row => {
          if(row("needUpdate").asBool) {
            row("updateMethod") := "auto"
          }
          row("age") ?= 20
          row("a") = row("b").asInt + row("c").asInt
        })
      }

      onSheet("Sheet2"){
        column("aaa").map(c => {
          c.row("id").asString + c.row("name").asString
        })
        
        sheet.addRow(List("2","tom","value for aaa"))

        column("age").foreach(c => {
          warn(c.asInt <=> 20, "20歳以下です") // ログのみ
          validate(c.asInt <= 10, "10歳以下は不正な値です") // 例外
        })
      }

    }

* [CheetSheet](docs/CheetSheet.md)

## その他

* [環境変数の設定](docs/Env.md)