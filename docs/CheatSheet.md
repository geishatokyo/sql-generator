
# CheetSheet

## Scope

|Code|説明|
|:--|:--|
|onSheet(_sheetName_: String){ }|名前が一致したシートで実行される|
|onSheet(_sheetNameRegex_: Regex){ }|正規表現に一致したシートで実行される|
|onAllSheet{ }|全てのシートで実行される|
|before{ }|シートの処理の前に実行される|
|after{ }|シートの処理の後に実行される|


## Variables

|Code|戻り値|説明|
|:--|:--|:--|
|workbook|Workbook|現在のworkbookを取得|
|context|Context|現在のContextを取得|
|sheet|Sheet|現在のScopeのSheetを取得|
|rows|List[Row]|現在のSheetのRowsを取得|
|columns|List[Column]|現在のSheetのColumnsを取得|
|column(_columnName_)|ColumnRef|Columnの実態では無く、Columnの参照を取得|
|now|ZonedDateTime|現在の時間を取得|
|today|ZonedDateTime|今日の0時の時間を取得|


## Workbook

|Code|戻り値|説明|
|:--|:--|:--|
|workbook.note|```Map[String,Any]```|処理中の情報を保持するObjectを取得|


## Sheet

|Code|戻り値|説明|
|:--|:--|:--|
|sheet(_sheetName_:String)|Sheet|シートを取得|
|workbook(_sheetName_:String)|Sheet|Sheetを取得|
|workbook.hasSheet(_sheetName_)|Boolean||
|workbook.getSheet(_sheetName_)|Option[Sheet]||
|createSheet(_sheetName_:String){ _init_ }|Unit|シートが存在しない場合作成し、_init_処理を行う。すでにシートが存在する場合には何もおきない|
|sheet.name = _newName_|Unit|シート名を変更|
|sheet.ignore = true or false|Unit|Sheet出力の無視設定の変更|

# Header


|Code|戻り値|説明|
|:--|:--|:--|
|sheet.headers|List[Header]||
|sheet.addHeader(_columnName_: String)|Unit|カラムを追加|
|sheet.addHeaders(_columnName_: String*)|Unit|カラムを複数追加|
|sheet.header(_columnName_:String)|Header||
|sheet.header(_index_:Int)|Header||
|header.isIgnore = true or false|||
|header.isId = true or false|||
|header.parent|Sheet||


## Row

|Code|戻り値|説明|
|:--|:--|:--|
|sheet.rows|List[Row]||
|sheet.addRow(_values_: Any*)||
|row.cells|List[Cell]||
|row(_index_)|Cell||
|row(_name_:String)|Cell||
|row.parent|Sheet||
|row.cells|List[Cell]|

## ColumnRef

|Code|戻り値|説明|
|:--|:--|:--|
|colRef := Any||全てのColumnのCellに値をセット|
|colRef ?= Any||全ての空のCellに値をセット|
|```colRef.foreach(Cell => Unit)```||全てのColumnのCellで処理をする|
|```colRef.map(Cell => Any)```||全てのColumnのCellを戻り値で置き換え|



## Cell

Cellの中身を気にせず演算等が出来るようになっています。

|Code|戻り値|説明|
|:--|:--|:--|
|cell.asInt|Int|Cellの値をIntへ変換|
|cell.asLong|Long||
|cell.asString|String||
|cell.asDouble|Double||
|cell.asBoolean|Boolean||
|cell.asDate|ZonedDateTime||
|cell.asOldDate|java.util.Date||
|cell.asDuration|Duration||
|cell.isEmpty|Boolean|cellが空かどうかを判定する|
|```cell [+-*/%&|] Any```|Any|いい感じの演算を行う|
|cell := Any||いい感じに代入する|
|cell ?= ANy||Cellが空の場合のみ代入する|

|cell.parent|Sheet||

## Query

簡易のQueryによる検索。
現在のWorkbook+外部workbookやDBなど全てから検索が可能


|Code|戻り値|説明|
|:--|:--|:--|
|select(_query_)|List[Row]||
|selectOne(_query_)|Row|Rowが見つからない場合は例外|
|exists(_query_)|Boolean||

|Code|
|:--|
|Query.from(_sheetName_).whereEq("id",21)|
|Query.from(_sheetName_).idOf(233)|
|```Query.from(_sheetName_).where(
    Eq("age",32) & Eq("gender","male")
)```|
|```Query.from(_sheetName_).where(
    Eq("age",22) | Eq("age", 20)
)```|
|```Query.from(_sheetName_).where(
    Range("age",10,20)
)```|
|```Query.from(_sheetName_).where(
    RegexMatch("name","Bob.*".r)
)```|
