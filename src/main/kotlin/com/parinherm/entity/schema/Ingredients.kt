package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.PersonDetails.references
import org.jetbrains.exposed.dao.id.LongIdTable

object Ingredients  : LongIdTable() {
    val name = varchar("name", 255)
    val quantity = double("quantity")
    val unit = varchar("unit", ApplicationData.lookupFieldLength)
    val recipeId = long("recipeId").references(Recipes.id)
}