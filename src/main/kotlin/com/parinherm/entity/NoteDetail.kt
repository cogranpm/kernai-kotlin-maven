package com.parinherm.entity

import kotlin.properties.Delegates

class NoteDetail (override var id: Long = 0, var noteHeaderId: Long, name: String, body: String, sourceCode: String, comments: String) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var body: String by Delegates.observable(body, observer)
    var sourceCode: String by Delegates.observable(sourceCode, observer)
    var comments: String by Delegates.observable(comments, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            else -> ""
        }
    }

    override fun toString(): String {
        return "NoteDetail(id=$id, noteHeaderId=$noteHeaderId, name=$name, sourceCode=$sourceCode, body=$body, comments=$comments)"
    }

    companion object Factory {
        fun make(noteHeaderId: Long): NoteDetail{
            return NoteDetail(
                0,
                noteHeaderId,
                "",
                "",
                "",
                ""
            )
        }
    }
}