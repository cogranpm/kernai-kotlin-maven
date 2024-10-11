package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class NoteSegment(override var id: Long = 0, var noteId: Long, noteSegmentTypeId: Long, title: String, body: String, bodyFile: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var noteSegmentTypeId: Long by Delegates.observable(noteSegmentTypeId, observer)
    var title: String by Delegates.observable(title, observer)
    var body: String by Delegates.observable(body, observer)
    var bodyFile: String by Delegates.observable(bodyFile, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> "$noteSegmentTypeId"
             1 -> title
             2 -> body
             3 -> bodyFile
             4 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "NoteSegment(id=$id, noteSegmentTypeId=$noteSegmentTypeId, title=$title, body=$body, bodyFile=$bodyFile, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(noteId: Long): NoteSegment{
            return NoteSegment(
                0,
                noteId,
                 0, 
                 "", 
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}