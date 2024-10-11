package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates
import java.time.LocalDateTime

class QuizRunQuestion(override var id: Long = 0, var quizRunId: Long, questionId: Long, audioblob: ByteArray, audiolength: Int, mark: Int, body: String ): ModelObject(), IBeanDataEntity  {

    var questionId: Long by Delegates.observable(questionId, observer)
    var audioblob: ByteArray by Delegates.observable(audioblob, observer)
    var audiolength: Int by Delegates.observable(audiolength, observer)
    var mark: Int by Delegates.observable(mark, observer)
    var body: String by Delegates.observable(body, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> "$questionId"
             1 -> ""
             2 -> "$audiolength"
             3 -> "$mark"
            4 -> body
            else -> ""
        }
    }

    override fun toString(): String {
        return "QuizRunQuestion(id=$id, questionId=$questionId, audioblob=$audioblob, audiolength=$audiolength, mark=$mark, body=$body)"
    }

    companion object Factory {
        fun make(QuizRunId: Long): QuizRunQuestion{
            return QuizRunQuestion(
                0,
                QuizRunId,
                 0 , 
                 ByteArray(0) , 
                 0 , 
                 0,
                ""
            )
        }
    }
}
