package com.parinherm.entity.schema

import com.parinherm.entity.Snippet
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object SnippetMapper : IMapper<Snippet> {

    val table = Snippets

    override fun save(item: Snippet) {
        MapperHelper.save(item, table, ::mapItem)
    }

    private fun mapItem(item: Snippet, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.language] = item.language
        statement[table.category] = item.category
        statement[table.topic] = item.topic
        statement[table.type] = item.type
        statement[table.desc] = item.desc
        statement[table.body] = item.body
        statement[table.output] = item.output
        statement[table.canRun] = item.canRun
    }

    override fun getAll(keys: Map<String, Long>): List<Snippet> {
        return MapperHelper.getAll(
                keys,
                table,
                null,
                table.name to SortOrder.ASC,

                )
        {
            Snippet(
                    it[table.id].value,
                    it[table.name],
                    it[table.language],
                    it[table.category],
                    it[table.topic],
                    it[table.type],
                    it[table.desc],
                    it[table.body],
                    it[table.output],
                    it[table.canRun]
            )
        }
    }

    override fun delete(item: Snippet) {
        MapperHelper.delete(table, table.id eq item.id)
    }
}