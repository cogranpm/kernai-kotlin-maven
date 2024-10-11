package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class QuizRun(override var id: Long = 0, var quizId: Long, CreatedDate: LocalDateTime): ModelObject(), IBeanDataEntity  {

    var CreatedDate: LocalDateTime by Delegates.observable(CreatedDate, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> "${CreatedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}"
            else -> ""
        }
    }

    override fun toString(): String {
        return "QuizRun(id=$id, CreatedDate=$CreatedDate)"
    }

    companion object Factory {
        fun make(QuizId: Long): QuizRun{
            return QuizRun(
                0,
                QuizId,
                 LocalDateTime.now() 
                
            )
        }
    }
}
