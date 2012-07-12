


# Grammer

basic

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
           column {columnName} convert { String => String }
           column {columnName} throws error whenNotExists
                                            whenEmpty
                                            when { String => Boolean }

scoping

    onSheet({SheetName}) { ... }
