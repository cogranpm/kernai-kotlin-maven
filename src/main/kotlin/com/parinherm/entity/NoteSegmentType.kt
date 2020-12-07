package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class NoteSegmentType(override var id: Long = 0, var noteSegmentTypeHeaderId: Long, title: String, fontDesc: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var fontDesc: String by Delegates.observable(fontDesc, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             1 -> fontDesc
             2 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "NoteSegmentType(id=$id, title=$title, fontDesc=$fontDesc, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(): NoteSegmentType{
            return NoteSegmentType(
                0,
                0,
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}