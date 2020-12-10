package com.parinherm.entity.schema

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object SchemaBuilder {

    val db = Database.connect(
        "jdbc:postgresql://DESKTOP-0UGB49O/golangtest",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "REDDINGO"
    )

    fun build() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                Persons,
                PersonDetails,
                Recipes,
                Ingredients,
                Snippets,
                Logins,
                Notebooks,
                NoteHeaders,
                NoteDetails,
                Lookups,
                LookupDetails,
                Shelfs,
                Subjects,
                Publications,
                Topics,
                Notes,
                NoteSegmentTypeHeaders,
                NoteSegmentTypes
            )

        }
    }
}

