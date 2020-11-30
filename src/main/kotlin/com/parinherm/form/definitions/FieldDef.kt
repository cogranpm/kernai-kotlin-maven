package com.parinherm.form.definitions

import kotlinx.serialization.Serializable


@Serializable
data class FieldDef(
    val name: String,
    val title: String,
    val required: Boolean,
    val sizeHint: SizeDef,
    val dataTypeDef: DataTypeDef,
    val lookupKey: String? = null,
    val filterable: Boolean = false
) {
    val schemaType: String
    get() {
        return when (dataTypeDef) {
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE -> "text"
            DataTypeDef.TEXT,
            DataTypeDef.LOOKUP -> "varchar"
            DataTypeDef.FLOAT -> "double"
            DataTypeDef.MONEY -> "decimal"
            DataTypeDef.INT -> "integer"
            DataTypeDef.DATETIME -> "date"
            DataTypeDef.BOOLEAN -> "bool"
        }
    }

    val schemaDeclaration: String
    get() {
        return this.schemaType + "(\"$name\"" + when (dataTypeDef) {
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE -> ""
            DataTypeDef.TEXT -> ", 255"
            DataTypeDef.LOOKUP -> ", ApplicationData.lookupFieldLength"
            DataTypeDef.FLOAT -> ""
            DataTypeDef.MONEY -> ", 10, 2"
            DataTypeDef.INT -> ""
            DataTypeDef.DATETIME -> ""
            DataTypeDef.BOOLEAN -> ""
        } + ")"
    }
}