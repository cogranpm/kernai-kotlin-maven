package com.parinherm.entity

import kotlin.properties.Delegates

class Lookup (override var id: Long = 0,  key: String, label: String, encrypted: Boolean): ModelObject(), IBeanDataEntity  {
    var key: String by Delegates.observable(key, observer)
    var label: String by Delegates.observable(label, observer)
    var encrypted: Boolean by Delegates.observable(encrypted, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> key
            1 -> label
            2 -> if (encrypted) "True" else "False"
            else -> ""
        }
    }


    override fun toString(): String {
        return "Lookup(id=$id, key=$key, label=$label, encrypted=$encrypted)"
    }

    companion object Factory {
        fun make(): Lookup {
            return Lookup(
                0,
                "",
                "",
                false
            )
        }
    }
}