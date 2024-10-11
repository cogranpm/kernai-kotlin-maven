package com.parinherm.entity.schema

import com.parinherm.ApplicationData

import com.parinherm.entity.Question

import com.parinherm.entity.Answer
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.LocalDateTime

object AnswerMapper : IMapper<Answer> {

    val table = Answers

    override fun save(item: Answer) {
        MapperHelper.save(item, table, AnswerMapper::mapItem)
    }

    private fun mapItem(item: Answer, statement: UpdateBuilder<Int>) {
        statement[table.body] = item.body
        statement[table.bodyFile] = item.bodyFile
        statement[table.answerType] = item.answerType
        statement[table.createdDate] = item.createdDate
        statement[table.audioBlob] = ExposedBlob(item.audioBlob)
        statement[table.audioLength] = item.audioLength
        statement[table.questionId] = item.questionId

    }

    /*
    override fun getAll(keys: Map<String, Long>): List<Answer> {
    return MapperHelper.getAll(keys,
    table,
     table.questionId eq keys["questionId"] as Long 
    ,  table.body to SortOrder.ASC      ) {
            Answer(it[table.id].value,
                it[table.questionId],
it[table.body]?:"", 
                it[table.bodyFile]?:"", 
                it[table.answerType]?:0, 
                it[table.createdDate]?:LocalDateTime.now(), 
                it[table.audioBlob]?.bytes?:ByteArray(0),
                it[table.audioLength]?:0
                
            )
        }
    }
     */

    override fun getAll(keys: Map<String, Long>): List<Answer> {
        return MapperHelper.getAll(
            keys,
            table,
            table.questionId eq keys["questionId"] as Long, table.body to SortOrder.ASC
        ) {
            Answer(
                it[table.id].value,
                it[table.questionId],
                it[table.body] ?: "",
                it[table.bodyFile] ?: "",
                it[table.answerType] ?: 0,
                it[table.createdDate] ?: LocalDateTime.now(),
                it[table.audioBlob]?.bytes ?: ByteArray(0),
                it[table.audioLength] ?: 0
            )
        }
    }


    fun delete(parent: Question) {
        MapperHelper.delete(table, table.questionId eq parent.id)
    }


    override fun delete(item: Answer) {

        MapperHelper.delete(table, table.id eq item.id)
    }
}
