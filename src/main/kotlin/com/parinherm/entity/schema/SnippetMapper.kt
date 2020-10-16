package com.parinherm.entity.schema

import com.parinherm.entity.Snippet
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object SnippetMapper : IMapper<Snippet> {

    override fun save(item: Snippet) {
        MapperHelper.save(item, Snippets, SnippetMapper::mapItem)
   }

    private fun mapItem(item: Snippet, statement: UpdateBuilder<Int>) {
        statement[Snippets.name] = item.name
        statement[Snippets.language] = item.language
        statement[Snippets.category] = item.category
        statement[Snippets.topic] = item.topic
        statement[Snippets.type] = item.type
        statement[Snippets.desc] = item.desc
        statement[Snippets.body] = item.body
    }

    override fun getAll(keys: Map<String, Long>): List<Snippet> {
        val items: MutableList<Snippet> = mutableListOf()
        transaction {
            val query: Query = Snippets.selectAll()
            query.orderBy(Snippets.name to SortOrder.ASC)
            query.forEach {
                items.add(
                    Snippet(
                        it[Snippets.id].value,
                        it[Snippets.name],
                        it[Snippets.language],
                        it[Snippets.category],
                        it[Snippets.topic],
                        it[Snippets.type],
                        it[Snippets.desc],
                        it[Snippets.body]
                    )
                )
            }
        }
        return items
    }
}