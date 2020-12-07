package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class NoteSegmentTypeHeader(override var id: Long = 0,  title: String, helpText: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var helpText: String by Delegates.observable(helpText, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             1 -> helpText
             2 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "NoteSegmentTypeHeader(id=$id, title=$title, helpText=$helpText, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(): NoteSegmentTypeHeader{
            return NoteSegmentTypeHeader(
                0,
                
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}