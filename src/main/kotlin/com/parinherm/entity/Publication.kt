package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import java.time.LocalDate
import kotlin.properties.Delegates

class Publication(override var id: Long = 0, var subjectId: Long, title: String, type: String, comments: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var type: String by Delegates.observable(type, observer)
    var comments: String by Delegates.observable(comments, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             1 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.publicationTypeLookupKey, false).find { it.code == type}
                "${listItem?.label}"
            }
             //2 -> comments
             2 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Publication(id=$id, title=$title, type=$type, comments=$comments, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(subjectId: Long): Publication{
            return Publication(
                0,
                subjectId,
                 "",
                LookupUtils.getLookupByKey(LookupUtils.publicationTypeLookupKey, false)[0].code,
                 "", 
                 LocalDate.now()
            )
        }
    }
}