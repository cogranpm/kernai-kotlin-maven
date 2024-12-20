/*
the type of fields a view can have
LOOKUP is a combo box that reads a predefined list of lookup records in database
SOURCE is for source code, and displays in a special editor
REFERENCE is when a field in a view refers to the entity contained in another view

 */

package com.parinherm.form.definitions

enum class DataTypeDef {
    TEXT, MEMO, FLOAT, INT, LOOKUP, BOOLEAN, DATETIME, DATE, TIME, MONEY, SOURCE, REFERENCE, FILE, BLOB;

    val dataTypeToKotlinDef: String
        get() = when (this) {
            MEMO, SOURCE, TEXT, LOOKUP, FILE -> "String"
            FLOAT -> "Double"
            MONEY -> "BigDecimal"
            INT -> "Int"
            DATETIME -> "LocalDateTime"
            TIME -> "LocalDateTime"
            DATE -> "LocalDate"
            BOOLEAN -> "Boolean"
            REFERENCE -> "Long"
            BLOB -> "ByteArray"
        }

    val dataTypeToTypeScriptDef: String
        get() = when (this) {
            MEMO, SOURCE, TEXT, LOOKUP, FILE, DATETIME, TIME, DATE -> "string"
            FLOAT, MONEY, INT, REFERENCE, BLOB -> "number"
            BOOLEAN -> "boolean"
        }

    val dataTypeToCSharpDef: String
        get() = when (this) {
            MEMO, SOURCE, TEXT, LOOKUP, FILE -> "String"
            FLOAT -> "float"
            MONEY -> "decimal"
            INT -> "int"
            DATETIME -> "DateTime"
            TIME -> "DateTime"
            DATE -> "DateTime"
            BOOLEAN -> "bool"
            REFERENCE -> "long"
            BLOB -> "byte[]"
        }


    val schemaType: String
        get() {
            return when (this) {
                MEMO,
                SOURCE -> "text"
                TEXT,
                LOOKUP,
                FILE -> "varchar"
                FLOAT -> "double"
                MONEY -> "decimal"
                INT -> "integer"
                DATETIME -> "datetime"
                TIME -> "time"
                DATE -> "date"
                BOOLEAN -> "bool"
                REFERENCE -> "long"
                BLOB -> "blob"
            }
        }

    val schemaDeclaration: String
        get() {
            return when (this) {
                TEXT -> ", 255"
                LOOKUP -> ", LookupUtils.lookupFieldLength"
                MONEY -> ", 10, 2"
                INT, REFERENCE, SOURCE, MEMO, FLOAT, DATETIME, DATE, BOOLEAN, TIME -> ""
                FILE -> ", 3999"
                BLOB -> ""
            }
        }



    companion object {
        fun mappedDataType(dataType: DataTypeDef) = when (dataType) {
            DataTypeDef.TEXT -> {
                "TEXT"
            }
            DataTypeDef.MEMO -> {
                "MEMO"
            }
            DataTypeDef.SOURCE -> {
                "SOURCE"
            }
            DataTypeDef.FLOAT -> {
                "FLOAT"
            }
            DataTypeDef.MONEY -> {
                "MONEY"
            }
            DataTypeDef.INT -> {
                "INT"
            }
            DataTypeDef.BOOLEAN -> {
                "BOOLEAN"
            }
            DataTypeDef.DATE -> {
                "DATE"
            }
            DataTypeDef.DATETIME -> {
                "DATETIME"
            }
            DataTypeDef.TIME -> {
                "TIME"
            }
            DataTypeDef.LOOKUP -> {
                "LOOKUP"
            }
            DataTypeDef.REFERENCE -> {
                "REFERENCE"
            }
            DataTypeDef.FILE -> {
                "TEXT"
            }
            DataTypeDef.BLOB -> {
                "BLOB"
            }
        }

        fun unMappedDataType(raw: String) = when (raw) {
            "TEXT" -> DataTypeDef.TEXT
            "MEMO" -> DataTypeDef.MEMO
            "SOURCE" -> DataTypeDef.SOURCE
            "FLOAT" -> DataTypeDef.FLOAT
            "MONEY" -> DataTypeDef.MONEY
            "INT" -> DataTypeDef.INT
            "BOOLEAN" -> DataTypeDef.BOOLEAN
            "DATE" -> DataTypeDef.DATE
            "DATETIME" -> DataTypeDef.DATETIME
            "TIME" -> DataTypeDef.TIME
            "LOOKUP" -> DataTypeDef.LOOKUP
            "REFERENCE" -> DataTypeDef.REFERENCE
            "FILE" -> DataTypeDef.FILE
            "BLOB" -> DataTypeDef.BLOB
            else -> TEXT
        }
    }
}