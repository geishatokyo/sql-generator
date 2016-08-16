# SQL Generator

This library supports generating SQL from Excel sheets (.xls), CSV files and other formats.

# Getting started

First, install sbt and giter8.

You can create a simple project with a giter8 template.<br />
Download template at the directory where the source .xls are.

		> g8 geishatokyo/sql-gen

Then

		> sbt run

The library finds all the .xls files in the directory and generates insert, delete and update SQL files from them.

# Data sheet formats

## Excel (.xls) files

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
      
        files("hoge.xls","fuga.csv","aaa.xlsx") >>
          merge >> imports("dir_path/for/reference_files") >> 
          YourProject >>
          asXls.toDir("output/xls") >> asMySQL.toDir("output/sql")
         
      }

      addSheet("NewSheet");
      
      onSheet("SheetToIgnore"){
        ignore
      }

      onSheet("Sheet1"){
        column("column1").map( c => "Map " + c.asString)
        column("column2") := "set to all rows"
        column("column3").setIfEmpty(0)
        column("column4").name = "NewColumnName"
      }

      onSheet("Sheet2"){
        column("aaa").map(c => {
          c.row("id").asString + c.row("name").asString
        })
        
        sheet.addRow(List("2","tom","value for aaa"))
      }

    }


