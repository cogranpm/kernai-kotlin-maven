package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteDetail
import com.parinherm.entity.NoteHeader
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object NoteHeaderMapper : IMapper<NoteHeader> {

    val table = NoteHeaders

    override fun save(item: NoteHeader) {
        MapperHelper.save(item, table, ::mapItem)
    }

    private fun mapItem(item: NoteHeader, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.comments] = item.comments
        statement[table.notebookId] = item.notebookId
    }

    override fun getAll(keys: Map<String, Long>): List<NoteHeader> {
        return MapperHelper.getAll(keys,
            table,
            table.notebookId eq keys["notebookId"] as Long,
            table.name to SortOrder.ASC

        ) { r: ResultRow ->
            NoteHeader(
                r[table.id].value,
                r[table.notebookId],
                r[table.name],
                r[table.comments]
            )
        }
    }
}