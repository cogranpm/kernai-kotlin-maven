package com.parinherm.entity.schema

import com.parinherm.entity.schema.BeanTest
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

            SchemaUtils.create(BeanTest, PersonDetail)

            val test = BeanTest.insertAndGetId{
                it[name] = "Fred Farquar"
                it[income] = BigDecimal("1000.45")
                it[age] = 33
                it[height] = 6.40
                it[deceased] = true
                it[country] = "Aus"
                it[enteredDate] = LocalDate.now()
            }

            val beansy = PersonDetail.insertAndGetId {
                it[beanTestId] = test
                it[nickname] = "beansy"
                it[petSpecies] = "M"
            }

            val franko = PersonDetail.insertAndGetId {
                it[beanTestId] = test
                it[nickname] = "Franko"
                it[petSpecies] = "C"
            }

            val query: Query = BeanTest.selectAll()
            query.orderBy(BeanTest.enteredDate to SortOrder.ASC)
            query.forEach {
                println(it[BeanTest.name])
            }

        }

    }
}