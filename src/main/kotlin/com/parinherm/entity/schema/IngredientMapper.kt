package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object IngredientMapper : IMapper<Ingredient> {

    val table = Ingredients

    override fun save(item: Ingredient) {
        MapperHelper.save(item, table, IngredientMapper::mapItem)
   }

    private fun mapItem(item: Ingredient, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.quantity] = item.quantity
        statement[table.unit] = item.unit
        statement[table.recipeId] = item.recipeId
    }

    override fun getAll(keys: Map<String, Long>): List<Ingredient> {
        val list: MutableList<Ingredient> = mutableListOf()
        transaction {
            val query: Query = table.select { table.recipeId eq keys["recipeId"] as Long }
            query.orderBy(table.name to SortOrder.ASC)
            query.forEach {
                list.add(
                        Ingredient(it[table.id].value,
                                it[table.name],
                                it[table.quantity],
                                it[table.unit],
                                it[table.recipeId]
                        )
                )
            }
        }
        return list
    }
}