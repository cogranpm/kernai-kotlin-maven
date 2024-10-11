package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.custom.LongBlobColumnType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

object QuizRunQuestions : LongIdTable() {
    val questionId = long("questionId")
    val audioblob = registerColumn<ExposedBlob>("audioblob", LongBlobColumnType()).nullable()
    val audiolength = integer("audiolength").nullable()
    val mark = integer("mark").nullable()
    val quizRunId = long("quizRunId").references(QuizRuns.id)
    val body = text("body").nullable()
}
