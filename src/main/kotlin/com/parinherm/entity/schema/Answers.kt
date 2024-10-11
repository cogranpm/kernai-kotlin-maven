package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.custom.LongBlobColumnType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

object Answers : LongIdTable() {
    val body = text("body").nullable()
    val bodyFile = varchar("bodyFile", 255).nullable()
    val answerType = integer("answerType").nullable()
    val createdDate = datetime("createdDate").nullable()
    val audioBlob = registerColumn<ExposedBlob>("audioBlob", LongBlobColumnType()).nullable()
    //val audioBlob = blob("audioBlob").nullable()
    val audioLength = integer("audioLength").nullable()
    val questionId = long("questionId").references(Questions.id)
}
