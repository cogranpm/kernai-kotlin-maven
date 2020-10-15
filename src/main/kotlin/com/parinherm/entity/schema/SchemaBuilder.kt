package com.parinherm.entity.schema

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaBuilder {

    val db = Database.connect(
        "jdbc:postgresql://kronmintdesktop/golangtest",
        driver = "org.postgresql.Driver",
        user = "paulm",
        password = "reddingo"
    )

    fun build() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Persons, PersonDetails, Recipes, Ingredients, Snippets, Logins, Notebooks)
        }
    }
}

