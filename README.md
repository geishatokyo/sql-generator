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
        ignore
      }

      onSheet("Sheet1"){
        column("column1").map( c => "Map " + c.asString)
        column("column2") := "set to all rows"
        column("column3") ?= "set to empty cell foreach rows"
        column("column4").setIfEmpty("Act almost same as ?=")
        column("column5").name = "NewColumnName"
        column("column6") := { "Set another cell value" + column("column1").asString }
      }

      onSheet("Sheet2"){
        column("aaa").map(c => {
          c.row("id").asString + c.row("name").asString
        })
        
        sheet.addRow(List("2","tom","value for aaa"))
      }

    }


## その他

* [環境変数の設定](docs/Env.md)