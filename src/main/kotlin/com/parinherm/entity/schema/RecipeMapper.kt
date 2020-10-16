package com.parinherm.entity.schema

import com.parinherm.entity.Recipe
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeMapper : IMapper<Recipe> {

    val table = Recipes

    override fun save(item: Recipe) {
        MapperHelper.save(item, table, RecipeMapper::mapItem)
    }

    fun mapItem(item: Recipe, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.method] = item.method
        statement[table.category] = item.category
    }

    override fun getAll(keys: Map<String, Long>): List<Recipe> {
        val items: MutableList<Recipe> = mutableListOf()
        transaction {
            val query: Query = table.selectAll()
            query.orderBy(table.name to SortOrder.ASC)
            query.forEach {
                items.add(
                    Recipe(
                        it[table.id].value,
                        it[table.name],
                        it[table.method],
                        it[table.category]
                    )
                )
            }
        }
        return items
    }
}