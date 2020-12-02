package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class Topic(override var id: Long = 0, var publicationId: Long, name: String, comments: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var name: String by Delegates.observable(name, observer)
    var comments: String by Delegates.observable(comments, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> name
             1 -> comments
             2 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Topic(id=$id, name=$name, comments=$comments, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(): Topic{
            return Topic(
                0,
                0,
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}