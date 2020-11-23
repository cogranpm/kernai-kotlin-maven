package com.parinherm.model

import com.parinherm.form.definitions.DataTypeDef

class TemplateHelpers {
    fun translateDataTypeDef(dataTypeDef: DataTypeDef) : String =
        when(dataTypeDef) {
            DataTypeDef.MEMO, DataTypeDef.SOURCE, DataTypeDef.TEXT, DataTypeDef.LOOKUP -> "String"
            DataTypeDef.FLOAT -> "Double"
            DataTypeDef.MONEY -> "BigDecimal"
            DataTypeDef.INT -> "Int"
            DataTypeDef.DATETIME -> "LocalDate"
            DataTypeDef.BOOLEAN -> "Boolean"
        }
}