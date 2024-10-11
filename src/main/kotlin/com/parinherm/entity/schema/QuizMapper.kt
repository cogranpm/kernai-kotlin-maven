package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object QuizMapper : IMapper<Quiz> {

    val table = Quizs

    override fun save(item: Quiz) {
        MapperHelper.save(item, table, QuizMapper::mapItem)
    }

    private fun mapItem(item: Quiz, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.topicId] = item.topicId
        statement[table.createdDate] = item.createdDate
        statement[table.publicationId] = item.publicationId
    }

    override fun getAll(keys: Map<String, Long>): List<Quiz> {
        return MapperHelper.getAll(
            keys,
            table,
            table.publicationId eq keys["publicationId"] as Long, table.name to SortOrder.ASC
        ) {
            Quiz(
                it[table.id].value,
                it[table.publicationId],
                it[table.name],
                it[table.topicId] ?: 0,
                it[table.createdDate] ?: LocalDateTime.now()
            )
        }
    }

    fun delete(parent: Publication) {
        MapperHelper.delete(table, table.publicationId eq parent.id)
    }

    override fun delete(item: Quiz) {
        QuestionMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }
}