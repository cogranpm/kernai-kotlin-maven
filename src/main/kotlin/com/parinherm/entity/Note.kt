package com.parinherm.entity

import java.time.LocalDate
import kotlin.properties.Delegates

class Note(override var id: Long = 0, var topicId: Long, title: String, description: String, titleAudioFile: String, descriptionAudioFile: String, createdDate: LocalDate): ModelObject(), IBeanDataEntity  {

    var title: String by Delegates.observable(title, observer)
    var description: String by Delegates.observable(description, observer)
    var titleAudioFile: String by Delegates.observable(titleAudioFile, observer)
    var descriptionAudioFile: String by Delegates.observable(descriptionAudioFile, observer)
    var createdDate: LocalDate by Delegates.observable(createdDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> title
             //1 -> description
             1 -> titleAudioFile
             2 -> descriptionAudioFile
             3 -> "$createdDate"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Note(id=$id, title=$title, description=$description, titleAudioFile=$titleAudioFile, descriptionAudioFile=$descriptionAudioFile, createdDate=$createdDate)"
    }

    companion object Factory {
        fun make(topicId: Long): Note{
            return Note(
                0,
                topicId,
                 "", 
                 "", 
                 "", 
                 "", 
                 LocalDate.now()
                
            )
        }
    }
}