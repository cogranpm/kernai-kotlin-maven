package com.parinherm.entity

import com.parinherm.ApplicationData
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class Question(
    override var id: Long = 0,
    var quizId: Long,
    body: String,
    bodyFile: String,
    createdDate: LocalDateTime,
    audioBlob: ByteArray,
    audioLength: Int
) : ModelObject(), IBeanDataEntity {

    var body: String by Delegates.observable(body, observer)
    var bodyFile: String by Delegates.observable(bodyFile, observer)
    var createdDate: LocalDateTime by Delegates.observable(createdDate, observer)
    var audioBlob: ByteArray by Delegates.observable(audioBlob, observer)
    var audioLength: Int by Delegates.observable(audioLength, observer)


    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> body
            1 -> "${createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}"
            2 -> bodyFile
            3 -> "$audioLength"
            4 -> ""
            else -> ""
        }
    }

    override fun toString(): String {
        return "Question(id=$id, body=$body, bodyFile=$bodyFile, createdDate=$createdDate, audioBlob=$audioBlob, audioLength=$audioLength)"
    }

    companion object Factory {
        fun make(QuizId: Long): Question {
            return Question(
                0,
                QuizId,
                "",
                "",
                LocalDateTime.now(),
                ByteArray(0),
                0

            )
        }
    }
}
