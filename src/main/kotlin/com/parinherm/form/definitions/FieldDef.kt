package com.parinherm.form.definitions

import kotlinx.serialization.Serializable


@Serializable
data class FieldDef (val name: String,
                     val title: String,
                     val required: Boolean,
                     val sizeHint: SizeDef,
                     val dataTypeDef: DataTypeDef) {
}