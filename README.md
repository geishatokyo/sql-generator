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

# Structure

## Project class

You write settings to this class. See below for the setting DSL.<br />
You can extend the setting grammar with mixin Project traits.<br />
Convenient Project traits are placed in the package com.geishatokyo.sqlgen.project.

## Executor class

This class controls the processes that generate SQL.<br />
You can extend processes with mixin ProcessProvider traits.<br />
Convenient ProcessProvider traits are placed in the package com.geishatokyo.sqlgen.process.


# Grammar

## @BaseProject

Base

    ignore sheet  { sheetName }
           sheets { sheetName*}
           sheets_except { sheetName*}
           sheet by pf { PartialFunction[String,Boolean] }
           column  { columnName }
           columns { columnName* }
           columns_except {columnName*}
           column by pf { PartialFunction[String,Boolean] }

    map    sheetName  { (sheetName -> sheetName) }
           sheetNames { (sheetName -> sheetName)*}
           columnName { (columnName -> columnName) }
           columnNames { (columnName -> columnName)*}
           columnName by pf { PartialFunction[String,String] }

    ensure column {columnName} exists
           column {columnName} set {value} whenEmpty
                                           when { String => Boolean }
                                           always
           column {columnName} refer {columnName} whenEmpty
                                                  when { String => Boolean}
                                                  always
                                                  converting {String => String} *=> whenEmpty,when,always
           column {columnName} convert { String => String }
           column {columnName} throws error whenNotExists
                                            whenEmpty
                                            when { String => Boolean }
           sheet {sheetName} exists

    guess columnTypes { (String -> ColumnType.Value) *}
          columnType by pf { String => ColumnTypeValue}
          idColumn {columnName}
          idColumn by {String => Boolean}

Scoping sheet

    onSheet({sheetName}) { ... }

## @SheetAddress trait

ColumnAddress

    column({columnName}) at {SheetName}
                         @@ {SheetName}
    at({sheetName}) { ... }

## @MergeSplitProject

Merge and split

    # can declare outside of SheetScope
    merge sheet {sheetName} from {columnAddress*}
    split sheet {sheetName} into {columnAddress*}
    modify sheet {sheetName} rename {newName}
                             delete
    modify column {columnAddress} ... # => same as below

    # can declare only inside of SheetScope
    modify column {columnName} rename {newName}
                               convert { String => String }
                               delete
                               ignore
                               copyFrom {columnAddress}
                               copyTo   {columnAddress*}

## @ValidateProject

Validation

    # can declare outside of SheetScope
    validate eachRows {Row => Boolean}
    validate column {columnName} isNotEmpty
                                 isValidXML
                                 isSingleLine
                                 is { String => Boolean}
                                 is( notEmpty && validXml && singleLine && (f => f.lengh == 20))

## @ReferenceProject

Refer and set other sheet values.

    # columnName   -- column whose value will be set
    # sheetName    -- sheet (table) to be modified
    # where clause -- (MyRow,ReferRow) return match
    # value clause -- value computed from ReferRow when the _where_ function is true.
    set column {columnName} from {sheetName} where {(Row,Row) => Boolean} value {Row => String} whenEmpty
                                                                                                always
                                                                                                when { String => Boolean}



## Example

    class YourProject extends Project with ...{
      // These become global settings.
      map sheetName ("SheetName" -> "NewSheetName");
      ensure column "name" exists;

      // These settings are enabled on only chosen sheets.
      onSheet("SheetName"){
        guess idColumn "HogeID";
        validate column "name" is(v => v.startsWith("Mr."));
      }
      onSheet("FugaSheet"){
        map columnName by pf{
          case v if v.startsWith("_") => v.substring(1)
        }
      }

    }