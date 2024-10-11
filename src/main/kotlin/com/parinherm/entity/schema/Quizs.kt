package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.javatime.timestamp

object Quizs : LongIdTable() {
    val name = varchar("name", 255)
    val topicId = long("topicId").nullable()
    val createdDate = datetime("createdDate").nullable()
    val publicationId = long("publicationId").references(Publications.id)

}