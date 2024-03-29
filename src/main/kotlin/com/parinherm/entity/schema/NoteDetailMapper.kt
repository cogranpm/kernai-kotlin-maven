package com.parinherm.entity.schema

import com.parinherm.entity.NoteDetail
import com.parinherm.entity.NoteHeader
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object NoteDetailMapper : IMapper<NoteDetail> {

    val table = NoteDetails

    override fun save(item: NoteDetail) {
        MapperHelper.save(item, table, NoteDetailMapper::mapItem)
    }

    private fun mapItem(item: NoteDetail, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.body] = item.body
        statement[table.sourceCode] = item.sourceCode
        statement[table.comments] = item.comments
        statement[table.noteHeaderId] = item.noteHeaderId
    }

    override fun getAll(keys: Map<String, Long>): List<NoteDetail> {
        return MapperHelper.getAll(keys, table, table.noteHeaderId eq keys["noteHeaderId"] as Long, table.name to SortOrder.ASC ) {
            NoteDetail(it[table.id].value,
                it[table.noteHeaderId],
                it[table.name],
                it[table.body],
                it[table.sourceCode],
                it[table.comments])
        }
    }

    fun delete(parent: NoteHeader) {
        MapperHelper.delete(table, table.noteHeaderId eq parent.id)
    }

    override fun delete(item: NoteDetail) {
        MapperHelper.delete(table, table.id eq item.id)
    }

}