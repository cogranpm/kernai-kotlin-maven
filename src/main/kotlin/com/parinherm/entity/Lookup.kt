package com.parinherm.entity

import kotlin.properties.Delegates

class Lookup (override var id: Long = 0,  key: String, label: String): ModelObject(), IBeanDataEntity  {
    var key: String by Delegates.observable(key, observer)
    var label: String by Delegates.observable(label, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> key
            1 -> label
            else -> ""
        }
    }


    override fun toString(): String {
        return "LookupDetail(id=$id, key=$key, label=$label)"
    }

    companion object Factory {
        fun make(): Lookup {
            return Lookup(
                0,
                "",
                ""
            )
        }
    }
}