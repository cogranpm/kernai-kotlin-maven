package com.parinherm.entity

import com.parinherm.lookups.LookupUtils
import com.parinherm.pickers.TopicPicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class Quiz(override var id: Long = 0, var publicationId: Long, name: String, topicId: Long, createdDate: LocalDateTime): ModelObject(), IBeanDataEntity  {

    var name: String by Delegates.observable(name, observer)
    var topicId: Long by Delegates.observable(topicId, observer)
    var createdDate: LocalDateTime by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> name
             1 -> /*{
                     val listItem = TopicPicker.get(publicationId).find { it.id == topicId}
                     "${listItem?.name}"
                 }
                 */
                 "$topicId"
             2 -> "${createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}"
            else -> ""
        }
    }

    override fun toString(): String {
        return "Quiz(id=$id, name=$name, topicId=$topicId, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(publicationId: Long): Quiz{
            return Quiz(
                0,
                publicationId,
                 "", 
                 0, 
                 LocalDateTime.now()
                
            )
        }
    }
}