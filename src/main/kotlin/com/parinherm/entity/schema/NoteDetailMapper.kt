package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteDetail
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object NoteDetailMapper : IMapper<NoteDetail> {
    override fun save(item: NoteDetail) {
        MapperHelper.save(item, NoteDetails, NoteDetailMapper::mapItem)
        /*
        transaction {
            if (item.id == 0L) {
                val id = NoteDetails.insertAndGetId {
                    NoteDetailMapper.mapItem(item, it)
                }
                item.id = id.value
            } else {
                NoteDetails.update({ NoteDetails.id eq item.id }) {
                    NoteDetailMapper.mapItem(item, it)
                }
            }
        }

         */
    }

    private fun mapItem(item: NoteDetail, statement: UpdateBuilder<Int>) {
        statement[NoteDetails.name] = item.name
        statement[NoteDetails.body] = item.body
        statement[NoteDetails.sourceCode] = item.sourceCode
        statement[NoteDetails.comments] = item.comments
        statement[NoteDetails.noteHeaderId] = item.noteHeaderId
    }

    override fun getAll(keys: Map<String, Long>): List<NoteDetail> {
        val list: MutableList<NoteDetail> = mutableListOf()
        transaction {
            val query: Query = NoteDetails.select { NoteDetails.noteHeaderId eq keys["noteHeaderId"] as Long }
            query.orderBy(NoteDetails.name to SortOrder.ASC)
            query.forEach {
                list.add(
                    NoteDetail(it[NoteDetails.id].value,
                        it[NoteDetails.noteHeaderId],
                        it[NoteDetails.name],
                        it[NoteDetails.sourceCode],
                        it[NoteDetails.body],
                        it[NoteDetails.comments]
                    )
                )
            }
        }
        return list
    }

}