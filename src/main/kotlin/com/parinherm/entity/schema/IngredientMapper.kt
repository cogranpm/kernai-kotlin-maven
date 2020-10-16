package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object IngredientMapper : IMapper<Ingredient> {
    override fun save(item: Ingredient) {
        MapperHelper.save(item, Ingredients, IngredientMapper::mapItem)
   }

    private fun mapItem(item: Ingredient, statement: UpdateBuilder<Int>) {
        statement[Ingredients.name] = item.name
        statement[Ingredients.quantity] = item.quantity
        statement[Ingredients.unit] = item.unit
        statement[Ingredients.recipeId] = item.recipeId
    }

    override fun getAll(keys: Map<String, Long>): List<Ingredient> {
        val list: MutableList<Ingredient> = mutableListOf()
        transaction {
            val query: Query = Ingredients.select { Ingredients.recipeId eq keys["recipeId"] as Long }
            query.orderBy(Ingredients.name to SortOrder.ASC)
            query.forEach {
                list.add(
                        Ingredient(it[Ingredients.id].value,
                                it[Ingredients.name],
                                it[Ingredients.quantity],
                                it[Ingredients.unit],
                                it[Ingredients.recipeId]
                        )
                )
            }
        }
        return list
    }
}