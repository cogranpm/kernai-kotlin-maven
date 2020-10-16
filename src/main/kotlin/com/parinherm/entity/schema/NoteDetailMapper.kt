package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import com.parinherm.entity.NoteDetail
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

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
                it[table.sourceCode],
                it[table.body],
                it[table.comments])
        }
    }

}