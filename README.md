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

    import com.geishatokyo.sqlgen.project2._
    object YourProject extends DefaultProject{

      def main(args : Array[String]) {
        inDir("hgoe") >> YourProject >> asXls
      }

      addSheet("NewSheet");

      onSheet("Sheet1"){
        forColumn("column1") map(v => "Map to " + v) ifEmpty;
        forColumn("column2") set("0") always;
        forColumn("column3") renameTo("NewColumnName");
      }

      onSheet("Sheet2"){
        forColumn("ref other column") set( {
          column("name") + "_" + sheet("Sheet3").searchIdIs( column("foreignKey") )("name")
        })

      }

    }


## Grammer


### Outside sheet rules

    onSheet( _sheetName){
      ...Inside sheet rules
    }

    addSheet( _sheetName)

    ignore( sheet(_sheetName) )
    ignore( coluimn(_column) )
    guessColumnType( {
      case _patten => _ColumnType
    })
    guessId( _columnName => _isId)



### Inside sheet rules

only use in onSheet scope.

    forColumn( _columnName) map( _columnValue => _newColumnValue) when( _columnValue => _bool)
                                                                  ifEmpty
                                                                  always
                            set( _newColumnValue)                 when( _columnValue => _bool)
                                                                  ifEmpty
                                                                  always
                            ignore
                            isId
                            type_=( _ColumnType)
                            renameTo(_newColumnName)

    renameTo(_newSheetName)

If you don't set when,ifEmpty, nor always, ifEmpty is default.

### Refer other sheet or column

    sheet( _sheetName) search( _row => _bool) : Row
                       searchIdIs( _idValue) : Row
                       find( _row => _bool) : Option[Row]
                       findIdIs( _idValue) : Option[Row]

    column( _columnName) : String

    _row( _rowName) : String



### Processing

       Input                 Project         Output
    inDir( _dir)                            console
    file(_file)         >>  _Project     >> asXls
    workbook(_workbook)                     asSql


