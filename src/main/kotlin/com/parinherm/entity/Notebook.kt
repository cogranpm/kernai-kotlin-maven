package com.parinherm.entity

import kotlin.properties.Delegates

class Notebook(override var id: Long = 0, name: String, comments: String) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var comments: String by Delegates.observable(comments, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
           else -> ""
        }
    }

    override fun toString(): String {
        return "Notebook(id=$id, name=$name,  comments=$comments)"
    }

    companion object Factory {
        fun make(): Notebook{
            return Notebook(
                0,
                "",
                ""
            )
        }
    }

}