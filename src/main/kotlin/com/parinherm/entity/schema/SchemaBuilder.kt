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

            val test = BeanTest.insert{
                it[name] = "Fred Farquar"
                it[income] = BigDecimal("1000.45")
                it[age] = 33
                it[height] = 6.40
                it[deceased] = true
                it[country] = "Aus"
                it[enteredDate] = LocalDate.now()
            } get BeanTest.id

            val beansy = PersonDetail.insert {
                it[beanTestId] = test
                it[nickname] = "beansy"
                it[petSpecies] = "M"
            }

            val franko = PersonDetail.insert {
                it[beanTestId] = test
                it[nickname] = "Franko"
                it[petSpecies] = "C"
            }

            println(test)
        }

    }
}