package com.parinherm.form.definitions

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class EntityDef(val name: String){
    val decapitalized: String
        get() = name.replaceFirstChar { it.lowercase(Locale.getDefault()) }

    val capitalized: String
        get() = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    val upperCased: String
        get() = name.uppercase(Locale.getDefault())
}
