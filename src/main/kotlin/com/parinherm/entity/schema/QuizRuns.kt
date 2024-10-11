package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime

object QuizRuns : LongIdTable() {
    val CreatedDate = datetime("CreatedDate").nullable()
    val quizId = long("quizId").references(Quizs.id)
}
