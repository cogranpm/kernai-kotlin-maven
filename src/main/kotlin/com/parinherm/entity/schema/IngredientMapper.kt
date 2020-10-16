package com.parinherm.entity.schema

import com.parinherm.entity.Ingredient
import com.parinherm.entity.Person
import com.parinherm.entity.Recipe
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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
        return MapperHelper.getAll(keys, table, table.recipeId eq keys["recipeId"] as Long, table.name to SortOrder.ASC ) {
            Ingredient(it[table.id].value,
                it[table.name],
                it[table.quantity],
                it[table.unit],
                it[table.recipeId]
            )
        }
    }

    fun delete(parent: Recipe) {
        MapperHelper.delete(table, table.recipeId eq parent.id)
    }

    override fun delete(item: Ingredient) {
        MapperHelper.delete(table, table.id eq item.id)
    }
}