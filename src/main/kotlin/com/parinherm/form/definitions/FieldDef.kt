package com.parinherm.form.definitions

import com.parinherm.ApplicationData
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.ViewDefinition
import com.parinherm.lookups.LookupUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


//@Serializable
data class FieldDef(
    val name: String,
    val title: String,
    val required: Boolean,
    val sizeHint: SizeDef,
    val dataTypeDef: DataTypeDef,
    val lookupKey: String? = null,
    val filterable: Boolean = false,
    val default: String = "",
    val config: String = "",
    val sequence: Int = 0,
    val length: Int? = 0,
    val referenceViewDefinition: ViewDefinition?,
    val referenceDef: ReferenceDef? = null
) {

    /******************************************
     * do not delete, properties used by templates, which are uncompiled text files
     */
    val configMap: Map<String, String>
        get() {
            if (this.config.isNotEmpty()) {
                val json = Json.parseToJsonElement(this.config)
                val map = json.jsonObject.toMutableMap().mapValues { it.value.jsonPrimitive.toString() }
                return map
            } else {
                return mapOf()
            }
        }


    val someValue: Map<String, String>
        get() {
            if (this.config.isNotEmpty()) {
                val json = Json.parseToJsonElement(this.config)
                val map = json.jsonObject.toMutableMap().mapValues { it.value.jsonPrimitive.toString() }
                return map
            } else {
                return mapOf()
            }
        }

    val lookups: List<LookupDetail>
        get() {
            if (this.lookupKey != null) {
                return LookupUtils.getLookupByKey(this.lookupKey, false)
            } else {
                return listOf()
            }
        }

    val columnValue: String
        get() = when (dataTypeDef) {

            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FILE -> name

            DataTypeDef.FLOAT,
            DataTypeDef.MONEY,
            DataTypeDef.INT,
            DataTypeDef.DATETIME,
            DataTypeDef.TIME,
            DataTypeDef.DATE,
            DataTypeDef.BOOLEAN,
            DataTypeDef.REFERENCE -> """"${'$'}${name}""""

            DataTypeDef.LOOKUP -> """ {
                val listItem = LookupUtils.getLookupByKey("${lookupKey}", ${if (required) "false" else "true"}).find { it.code == ${name}}
                "${'$'}{listItem?.label}"
            }"""

            DataTypeDef.BLOB -> ""
        }

    val defaultValue: String
        get() = when (dataTypeDef) {
            DataTypeDef.BLOB -> "ByteArray(0)"
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FILE -> "\"\""

            DataTypeDef.LOOKUP -> """LookupUtils.getLookupByKey("${lookupKey}", ${if (required) "false" else "true"})[0].code"""
            DataTypeDef.FLOAT -> "0.0"
            DataTypeDef.MONEY -> "BigDecimal(0.0)"
            DataTypeDef.INT -> "0"
            DataTypeDef.DATETIME -> "LocalDateTime.now()"
            DataTypeDef.TIME -> "LocalDateTime.now()"
            DataTypeDef.DATE -> "LocalDate.now()"
            DataTypeDef.BOOLEAN -> "false"
            DataTypeDef.REFERENCE -> "0"
        }

    val defaultJavascript: String
        get() = when (dataTypeDef) {
            DataTypeDef.BLOB -> "[]"
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FILE -> "''"

            DataTypeDef.LOOKUP -> "''"
            DataTypeDef.FLOAT -> "0.0"
            DataTypeDef.MONEY -> "0.0"
            DataTypeDef.INT -> "0"
            DataTypeDef.DATETIME -> "new Date()"
            DataTypeDef.TIME -> "'00:00:00'"
            DataTypeDef.DATE -> "new Date()"
            DataTypeDef.BOOLEAN -> "false"
            DataTypeDef.REFERENCE -> "''"
        }

    val defaultSqlTable: String
        get() = when (dataTypeDef) {
            DataTypeDef.BLOB -> "[]"
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FILE,
            DataTypeDef.LOOKUP,
            DataTypeDef.FLOAT,
            DataTypeDef.MONEY,
            DataTypeDef.INT,
            DataTypeDef.DATETIME,
            DataTypeDef.TIME,
            DataTypeDef.DATE -> when
            {
                default.isNotEmpty() -> " DEFAULT " + (when
                {
                    default.uppercase() == "YEAR" -> "YEAR(GETDATE())"
                    else -> default
                })
                else -> ""
            }
            DataTypeDef.BOOLEAN -> when
            {
                default.isNotEmpty() -> " DEFAULT " + (when
                {
                    default.uppercase() == "FALSE" -> "0"
                    default.uppercase() == "TRUE" -> "1"
                    else -> default
                })
                else -> ""
            }
            DataTypeDef.REFERENCE -> ""
        }


    val columnCompare: String
        get() = when (dataTypeDef) {
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FILE -> "compareString(entity1.${name}, entity2.${name})"

            DataTypeDef.LOOKUP -> """compareLookups(entity1.${name}, entity2.${name}, LookupUtils.getLookupByKey("${lookupKey}", ${if (required) "false" else "true"}))"""
            DataTypeDef.FLOAT,
            DataTypeDef.MONEY,
            DataTypeDef.INT,
            DataTypeDef.DATETIME,
            DataTypeDef.TIME,
            DataTypeDef.DATE,
            DataTypeDef.BOOLEAN,
            DataTypeDef.REFERENCE -> "entity1.${name}.compareTo(entity2.${name})"

            DataTypeDef.BLOB -> """compareString("a", "b")"""
        }

    val maxLength: Int
        get() =
            when (this.sizeHint) {
                SizeDef.LARGE -> 2000
                SizeDef.MEDIUM -> 750
                SizeDef.SMALL -> 150
            }

    val isDateType : Boolean
        get() =
            dataTypeDef == DataTypeDef.DATE
            || dataTypeDef == DataTypeDef.TIME
            || dataTypeDef == DataTypeDef.DATETIME

    val isCycleSearch : Boolean
        get() = configMap.getOrDefault("searchType", "") == "cycle"


    val nameAsField: String
        get() = this.name.replaceFirstChar { it.lowercase() }

    val lookupKeyAsField: String?
        get() = this.lookupKey?.replaceFirstChar { it.lowercase() }

    val cSharpNullablePostfix: String
        get() =
            when (this.dataTypeDef) {
                DataTypeDef.MEMO, DataTypeDef.SOURCE, DataTypeDef.TEXT, DataTypeDef.LOOKUP, DataTypeDef.FILE -> ""
                DataTypeDef.FLOAT -> "?"
                DataTypeDef.MONEY -> "?"
                DataTypeDef.INT -> "?"
                DataTypeDef.DATETIME -> "?"
                DataTypeDef.TIME -> "?"
                DataTypeDef.DATE -> "?"
                DataTypeDef.BOOLEAN -> "?"
                DataTypeDef.REFERENCE -> "?"
                DataTypeDef.BLOB -> "?"
                else -> ""
            }

    val dataTypeToSqlDef: String
        get() {
            var length: Int = 0;
            if (this.length != null && this.length > 0) {
                length = this.length
            } else {
                length = when (this.sizeHint) {
                    SizeDef.MEDIUM -> 500
                    SizeDef.SMALL -> 50
                    SizeDef.LARGE -> 1000
                }
            }
            return when (this.dataTypeDef) {

                DataTypeDef.SOURCE,
                DataTypeDef.TEXT,
                DataTypeDef.LOOKUP,
                DataTypeDef.FILE -> "NVARCHAR($length)"

                DataTypeDef.MEMO -> "NVARCHAR(MAX)"
                DataTypeDef.FLOAT -> "FLOAT"
                DataTypeDef.MONEY -> "DECIMAL (9, 2)"
                DataTypeDef.INT -> when(this.sizeHint)
                {
                    SizeDef.LARGE -> "BIGINT"
                    SizeDef.MEDIUM -> "INT"
                    SizeDef.SMALL -> "SMALLINT"
                    else -> "INT"
                }
                DataTypeDef.DATETIME -> "DATETIME2 (3)"
                DataTypeDef.TIME -> "DATETIME2 (3)"
                DataTypeDef.DATE -> "DATE"
                DataTypeDef.BOOLEAN -> "BIT"
                DataTypeDef.REFERENCE -> "LONG"
                DataTypeDef.BLOB -> "VARBINARY"
            }
        }

        val dataTypeToCSharpDef: String
        get() = when (this.dataTypeDef) {
            DataTypeDef.MEMO, DataTypeDef.SOURCE, DataTypeDef.TEXT, DataTypeDef.LOOKUP, DataTypeDef.FILE -> "string"
            DataTypeDef.FLOAT -> "float"
            DataTypeDef.MONEY -> "decimal"
            DataTypeDef.INT -> when (this.sizeHint) {
                    SizeDef.MEDIUM ->  "int"
                    SizeDef.SMALL -> "Int16"
                    SizeDef.LARGE -> "long"
                    else -> "int"
                }
            DataTypeDef.DATETIME -> "DateTime"
            DataTypeDef.TIME -> "DateTime"
            DataTypeDef.DATE -> "DateTime"
            DataTypeDef.BOOLEAN -> "bool"
            DataTypeDef.REFERENCE -> "long"
            DataTypeDef.BLOB -> "byte[]"
        }


}