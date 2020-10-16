package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteDetail
import com.parinherm.entity.NoteHeader
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object NoteHeaderMapper : IMapper<NoteHeader> {

    override fun save(item: NoteHeader) {
        transaction {
            if (item.id == 0L) {
                val id = NoteHeaders.insertAndGetId {
                    mapItem(item, it)
                }
                item.id = id.value
            } else {
                NoteHeaders.update({ NoteHeaders.id eq item.id }) {
                    mapItem(item, it)
                }
            }
        }
    }

    private fun mapItem(item: NoteHeader, statement: UpdateBuilder<Int>) {
        statement[NoteHeaders.name] = item.name
        statement[NoteHeaders.comments] = item.comments
        statement[NoteHeaders.notebookId] = item.notebookId
    }

    override fun getAll(keys: Map<String, Long>): List<NoteHeader> {
        val list: MutableList<NoteHeader> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = NoteHeaders.select { NoteHeaders.notebookId eq keys["notebookId"] as Long }
            query.orderBy(NoteHeaders.name to SortOrder.ASC)
            query.forEach {
                list.add(
                    NoteHeader(it[NoteHeaders.id].value,
                        it[NoteHeaders.notebookId],
                        it[NoteHeaders.name],
                        it[NoteHeaders.comments]
                    )
                )
            }
        }
        return list
    }
}