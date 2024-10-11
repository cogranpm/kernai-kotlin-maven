package com.parinherm.entity.schema

import com.parinherm.ApplicationData

import com.parinherm.entity.Quiz

import com.parinherm.entity.Question
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import kotlin.random.Random

object QuestionMapper : IMapper<Question> {

    val table = Questions

    override fun save(item: Question) {
        MapperHelper.save(item, table, QuestionMapper::mapItem)
    }

    private fun mapItem(item: Question, statement: UpdateBuilder<Int>) {
        statement[table.body] = item.body
        statement[table.bodyFile] = item.bodyFile
        statement[table.createdDate] = item.createdDate
        statement[table.audioBlob] = ExposedBlob(item.audioBlob)
        statement[table.audioLength] = item.audioLength
        statement[table.quizId] = item.quizId
    }

    private fun mapRow(row: ResultRow): Question {
        var audioBlob = ByteArray(0)
        if(row.hasValue(table.audioBlob)){
           audioBlob = row[table.audioBlob]?.bytes ?: ByteArray(0)
        }
        return Question(
            row[table.id].value,
            row[table.quizId],
            row[table.body] ?: "",
            row[table.bodyFile] ?: "",
            row[table.createdDate] ?: LocalDateTime.now(),
            audioBlob,
            row[table.audioLength] ?: 0
        )
    }

    override fun getAll(keys: Map<String, Long>): List<Question> {
        val list: MutableList<Question> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = table.slice(
                table.id,
                table.quizId,
                table.body,
                table.createdDate,
                table.audioLength,
                table.bodyFile
            ).select { table.quizId eq keys["quizId"] as Long }.limit(ApplicationData.maxRowsLimit)
            query.orderBy(table.createdDate to SortOrder.DESC)
            query.forEach {
                list.add(
                    mapRow(it)
                    /*
                    Question(
                        it[table.id].value,
                        it[table.quizId],
                        it[table.body] ?: "",
                        it[table.bodyFile] ?: "",
                        it[table.createdDate] ?: LocalDateTime.now(),
                        ByteArray(0),
                        it[table.audioLength] ?: 0
                    )
                     */
                )
            }
        }
        return list
        /*
        return MapperHelper.getAll(
            keys,
            table,
            table.quizId eq keys["quizId"] as Long, table.body to SortOrder.ASC
        ) {
            Question(
                it[table.id].value,
                it[table.quizId],
                it[table.body] ?: "",
                it[table.bodyFile] ?: "",
                it[table.createdDate] ?: LocalDateTime.now(),
                it[table.audioBlob]?.bytes ?: ByteArray(0),
                it[table.audioLength] ?: 0

            )
        }
         */
    }


    fun delete(parent: Quiz) {
        MapperHelper.delete(table, table.quizId eq parent.id)
    }

    fun getAllKeysByQuizId(quizId: Long): List<Long> {
        val list: MutableList<Long> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query =
                table.slice(table.id).select { table.quizId eq quizId }.limit(ApplicationData.maxRowsLimit)
            query.forEach {
                list.add(it[table.id].value)
            }
        }
        val shuffled = list.shuffled(Random.Default)
        return shuffled
    }

    override fun delete(item: Question) {
        AnswerMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun loadAudio(id: Long): ByteArray {
        return transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = table.slice(
                table.audioBlob
            ).select { table.id eq  id}.limit(1)
            val result = query.firstOrNull()
            if(result != null){
                return@transaction result[table.audioBlob]?.bytes ?: ByteArray(0)
            } else {
                return@transaction  ByteArray(0)
            }
        }
    }

    fun load(id: Long): Question? {
        return transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = table.select { table.id eq  id}.limit(1)
            val result = query.firstOrNull()
            if(result != null){
                return@transaction mapRow(result)
            } else {
                return@transaction null
            }
        }
    }
}
