package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class Subject(override var id: Long = 0, var shelfId: Long, title: String, comments: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var comments: String by Delegates.observable(comments, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             1 -> comments
             2 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Subject(id=$id, title=$title, comments=$comments, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(): Subject{
            return Subject(
                0,
                0,
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}