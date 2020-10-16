package com.parinherm.entity.schema

import com.parinherm.entity.NoteHeader
import com.parinherm.entity.Notebook
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

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

    fun delete(parent: Notebook) {
        MapperHelper.delete(table, table.notebookId eq parent.id)
    }

    override fun delete(item: NoteHeader) {
        NoteDetailMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }
}