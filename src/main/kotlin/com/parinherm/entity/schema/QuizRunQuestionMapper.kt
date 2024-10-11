package com.parinherm.entity.schema

import com.mysql.cj.protocol.ResultsetRow
import com.parinherm.ApplicationData
import com.parinherm.entity.Question

import com.parinherm.entity.QuizRun

import com.parinherm.entity.QuizRunQuestion
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object QuizRunQuestionMapper : IMapper<QuizRunQuestion> {

    val table = QuizRunQuestions

    override fun save(item: QuizRunQuestion) {
        MapperHelper.save(item, table, QuizRunQuestionMapper::mapItem)
    }

    private fun mapItem(item: QuizRunQuestion, statement: UpdateBuilder<Int>) {
        statement[table.questionId] = item.questionId
        statement[table.audioblob] = ExposedBlob(item.audioblob)
        statement[table.audiolength] = item.audiolength
        statement[table.mark] = item.mark
        statement[table.quizRunId] = item.quizRunId
        statement[table.body] = item.body
    }

    override fun getAll(keys: Map<String, Long>): List<QuizRunQuestion> {
        return MapperHelper.getAll(
            keys,
            table,
            table.quizRunId eq keys["quizRunId"] as Long,
           table.id to SortOrder.ASC
        ) {
            QuizRunQuestion(
                it[table.id].value,
                it[table.quizRunId],
                it[table.questionId],
                ByteArray(0),
                it[table.audiolength] ?: 0,
                it[table.mark] ?: 0,
                it[table.body]?:""
            )
        }
    }

    fun getRevisionQuestionsByQuizRunId(quizRunId: Long) : List<Long> {
        var results: List<ResultRow> = emptyList()
        transaction {
            results = table.select(table.questionId)
                .where { table.quizRunId eq quizRunId and (table.mark less 1) }
                .limit(ApplicationData.maxRowsLimit)
                .distinct()
        }
        return results.map { it[table.questionId] }
    }

    fun loadAudio(id: Long): ByteArray {
        return transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = table.slice(
                table.audioblob
            ).select { table.id eq  id}.limit(1)
            val result = query.firstOrNull()
            if(result != null){
                return@transaction result[table.audioblob]?.bytes ?: ByteArray(0)
            } else {
                return@transaction  ByteArray(0)
            }
        }
    }


    fun delete(parent: QuizRun) {
        MapperHelper.delete(table, table.quizRunId eq parent.id)
    }


    override fun delete(item: QuizRunQuestion) {
        MapperHelper.delete(table, table.id eq item.id)
    }
}
