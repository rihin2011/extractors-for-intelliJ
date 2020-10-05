SEP = ", "
QUOTE     = "\'"
NEWLINE   = System.getProperty("line.separator")

begin = true

def record(columns, dataRow) {

    if (begin) {
        OUT.append("INSERT INTO ")
        if (TABLE == null) OUT.append("MY_TABLE")
        else OUT.append(TABLE.getName())
        OUT.append(" (")

        columns.eachWithIndex { column, idx ->
            OUT.append(column.name()).append(idx != columns.size() - 1 ? SEP : "")
        }

        OUT.append(")").append(NEWLINE)
        OUT.append("VALUES").append("  (")
        begin = false
    }
    else {
        OUT.append(",").append(NEWLINE)
        OUT.append("        (")
    }



    columns.eachWithIndex { column, idx ->
        def typeName = FORMATTER.getTypeName(dataRow, column)
        def skipQuote = (typeName != "text" && dataRow.value(column).toString().isNumber()) || dataRow.value(column) == null
        def stringValue = FORMATTER.format(dataRow, column)
        if (typeName == "_text" && stringValue == ""){
            stringValue = "{}"
        }
        //if (DIALECT.getFamilyId().isMysql()) stringValue = stringValue.replace("\\", "\\\\")
        OUT.append(skipQuote ? "": QUOTE).append(stringValue.replace(QUOTE, QUOTE + QUOTE))
                .append(skipQuote ? "": QUOTE).append(idx != columns.size() - 1 ? SEP : "")
    }
    OUT.append(")")
}

ROWS.each { row -> record(COLUMNS, row) }

OUT.append(NEWLINE)
OUT.append(";")
