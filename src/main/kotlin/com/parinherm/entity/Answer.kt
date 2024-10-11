package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates
import java.time.LocalDateTime

class Answer(override var id: Long = 0, var questionId: Long, body: String, bodyFile: String, answerType: Int, createdDate: LocalDateTime, audioBlob: ByteArray, audioLength: Int): ModelObject(), IBeanDataEntity  {

    var body: String by Delegates.observable(body, observer)
    var bodyFile: String by Delegates.observable(bodyFile, observer)
    var answerType: Int by Delegates.observable(answerType, observer)
    var createdDate: LocalDateTime by Delegates.observable(createdDate, observer)
    var audioBlob: ByteArray by Delegates.observable(audioBlob, observer)
    var audioLength: Int by Delegates.observable(audioLength, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> body
             1 -> bodyFile
             2 -> "$answerType"
             3 -> "$createdDate"
             4 -> ""
             5 -> "$audioLength"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Answer(id=$id, body=$body, bodyFile=$bodyFile, answerType=$answerType, createdDate=$createdDate, audioBlob=$audioBlob, audioLength=$audioLength)"
    }

    companion object Factory {
        fun make(QuestionId: Long): Answer{
            return Answer(
                0,
                QuestionId,
                 "" , 
                 "" , 
                 0 , 
                 LocalDateTime.now() , 
                 ByteArray(0) , 
                 0 
                
            )
        }
    }
}
