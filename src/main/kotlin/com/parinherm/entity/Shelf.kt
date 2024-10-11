package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class Shelf(override var id: Long = 0,  title: String, comments: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var comments: String by Delegates.observable(comments, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             //1 -> comments
             1 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Shelf(id=$id, title=$title, comments=$comments, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(): Shelf{
            return Shelf(
                0,
                 "",
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}