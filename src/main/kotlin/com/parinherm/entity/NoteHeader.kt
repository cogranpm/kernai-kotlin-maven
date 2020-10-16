package com.parinherm.entity

import kotlin.properties.Delegates

class NoteHeader (override var id: Long = 0, var notebookId: Long, name: String, comments: String) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var comments: String by Delegates.observable(comments, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            else -> ""
        }
    }

    override fun toString(): String {
        return "NoteHeader(id=$id, notebookId=$notebookId, name=$name,  comments=$comments)"
    }

    companion object Factory {
        fun make(notebookId: Long): NoteHeader{
            return NoteHeader(
                0,
                 notebookId,
                "",
                ""
            )
        }
    }


}