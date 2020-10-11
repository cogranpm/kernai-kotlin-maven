package com.parinherm.entity.schema

import com.parinherm.entity.Recipe
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object RecipeMapper : IMapper<Recipe> {

    override fun save(item: Recipe) {
        transaction {
            addLogger(StdOutSqlLogger)
            if (item.id == 0L) {
                val id = Recipes.insertAndGetId {
                    mapItem(item, it)
                }
                item.id = id.value
            } else {
                Recipes.update({ Recipes.id eq item.id }) {
                    mapItem(item, it)
                }
            }
        }
    }

    fun mapItem(item: Recipe, statement: UpdateBuilder<Int>) {
        statement[Recipes.name] = item.name
        statement[Recipes.method] = item.method
        statement[Recipes.category] = item.category
    }

    override fun getAll(keys: Map<String, Long>): List<Recipe> {
        val items: MutableList<Recipe> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = Recipes.selectAll()
            query.orderBy(Recipes.name)
            query.forEach {
                items.add(
                    Recipe(
                        it[Recipes.id].value,
                        it[Recipes.name],
                        it[Recipes.method],
                        it[Recipes.category]
                    )
                )
            }
        }
        return items
    }
}