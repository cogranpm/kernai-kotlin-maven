package com.parinherm.model

import com.parinherm.ApplicationData
import com.parinherm.form.definitions.DataTypeDef
import com.parinherm.form.definitions.FieldDef
import com.parinherm.form.definitions.ViewDef

class TemplateHelpers {
    fun translateDataTypeDef(dataTypeDef: DataTypeDef): String =
        when (dataTypeDef) {
            DataTypeDef.MEMO, DataTypeDef.SOURCE, DataTypeDef.TEXT, DataTypeDef.LOOKUP -> "String"
            DataTypeDef.FLOAT -> "Double"
            DataTypeDef.MONEY -> "BigDecimal"
            DataTypeDef.INT -> "Int"
            DataTypeDef.DATETIME -> "LocalDate"
            DataTypeDef.BOOLEAN -> "Boolean"
        }


    fun translateColumnValue(fieldDef: FieldDef): String =
        when (fieldDef.dataTypeDef) {
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT,
            DataTypeDef.FLOAT,
            DataTypeDef.MONEY,
            DataTypeDef.INT,
            DataTypeDef.DATETIME,
            DataTypeDef.BOOLEAN -> fieldDef.name
            DataTypeDef.LOOKUP -> """ {
                val listItem = ApplicationData.${fieldDef.lookupKey}.find { it.code == ${fieldDef.name}}
                "${'$'}{listItem?.label}"
            }"""
        }

    fun translateDefaultValue(fieldDef: FieldDef): String =
        when (fieldDef.dataTypeDef) {
            DataTypeDef.MEMO,
            DataTypeDef.SOURCE,
            DataTypeDef.TEXT -> "\"\""
            DataTypeDef.LOOKUP -> "ApplicationData.${fieldDef.lookupKey}[0].code"
            DataTypeDef.FLOAT -> "0.0"
            DataTypeDef.MONEY -> "BigDecimal(0.0)"
            DataTypeDef.INT -> "0"
            DataTypeDef.DATETIME -> "LocalDate.now()"
            DataTypeDef.BOOLEAN -> "False"
        }



    fun getForeignKeysByView(viewDef: ViewDef): List<ViewDef> {
        return viewDef.parentViews.map {
            ApplicationData.getView(it)
        }
    }


}