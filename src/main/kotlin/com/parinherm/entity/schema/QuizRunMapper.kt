package com.parinherm.entity.schema

import com.parinherm.ApplicationData

import com.parinherm.entity.Quiz

import com.parinherm.entity.QuizRun
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.LocalDateTime

object QuizRunMapper : IMapper<QuizRun> {

    val table = QuizRuns

    override fun save(item: QuizRun) {
        MapperHelper.save(item, table, QuizRunMapper::mapItem)
    }

    private fun mapItem(item: QuizRun, statement: UpdateBuilder<Int>) {
        statement[table.CreatedDate] = item.CreatedDate

        statement[table.quizId] = item.quizId

    }

    override fun getAll(keys: Map<String, Long>): List<QuizRun> {
        return MapperHelper.getAll(
            keys,
            table,
            table.quizId eq keys["quizId"] as Long, table.CreatedDate to SortOrder.ASC
        ) {
            QuizRun(
                it[table.id].value,
                it[table.quizId],
                it[table.CreatedDate] ?: LocalDateTime.now()

            )
        }
    }


    fun delete(parent: Quiz) {
        MapperHelper.delete(table, table.quizId eq parent.id)
    }


    override fun delete(item: QuizRun) {
        QuizRunQuestionMapper.delete(item)

        MapperHelper.delete(table, table.id eq item.id)
    }
}
