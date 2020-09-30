package com.parinherm.entity.schema

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal
import java.time.LocalDate

object SchemaBuilder {

    val db = Database.connect("jdbc:postgresql://kronmintdesktop/golangtest",
        driver = "org.postgresql.Driver",
        user="paulm",
        password = "reddingo")


    fun build() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(BeanTests, PersonDetails)
        }
    }

    fun test() {

        transaction {
            addLogger(StdOutSqlLogger)
            val test = BeanTests.insertAndGetId {
                it[name] = "Fred Farquar"
                it[income] = BigDecimal("1000.45")
                it[age] = 33
                it[height] = 6.40
                it[deceased] = true
                it[country] = "Aus"
                it[enteredDate] = LocalDate.now()
            }

            val beansy = PersonDetails.insertAndGetId {
                it[beanTestId] = test
                it[nickname] = "beansy"
                it[petSpecies] = "M"
            }

            val franko = PersonDetails.insertAndGetId {
                it[beanTestId] = test
                it[nickname] = "Franko"
                it[petSpecies] = "C"
            }

            val query: Query = BeanTests.selectAll()
            query.orderBy(BeanTests.enteredDate to SortOrder.ASC)
            query.forEach {
                println(it[BeanTests.name])
            }
        }

    }
}