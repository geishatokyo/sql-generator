# SQL Generator

This library supports generating sql from Excel file(.xls),CSV and so on.

# Getting started

First you install sbt and giter8.

You can create simple project by giter8 template.<br />
You download template at directory where data xls are put.

   g8 geishatokyo/sql-gen

Then

   sbt run

It searches all xls file in same directory and generate insert,delete,update sqls from them.

# Data sheet formats

## Excel file

Sheet corresponds with Database Table.

So sheet name should be table name.<br />
In sheet, first row must be column names, and after rows are record.

# Structure

## Project class

You write settings to this class.There is prepared DSL for setting.See below.<br />
You can extend setting grammer by mixin Project traits.<br />
Convenient project traits are placed in the package com.geishatokyo.sqlgen.project.

## Executor class

This class controls sql generating processes.<br />
You can extend processes by mixin ProcessProvider traits.<br />
Convenient ProcessProvider traits are placed under the package com.geishatokyo.sqlgen.process.


# Grammer

## @BaseProject

Base

    ignore sheet  { sheetName }
           sheets { sheetName*}
           sheet by pf { PratialFunction[String,Boolean] }
           column  { columnName }
           columns { columnNames }
           column by pf { PratialFunction[String,Boolean] }

    map    sheetName  { (sheetName -> sheetName) }
           sheetNames { (sheetName -> sheetName)*}
           columnName { (columnName -> columnName) }
           columnNames { (columnName -> columnName)*}
           columnName by pf { PratialFunction[String,String] }

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

## For example

    class YourProject extends Project with ...{
      // These become global setting.
      map sheetName ("SheetName" -> "NewSheetName");
      ensure column "name" exists;

      // These settings are enabled on only select sheet
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