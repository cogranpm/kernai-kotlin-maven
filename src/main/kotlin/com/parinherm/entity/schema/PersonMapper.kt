package com.parinherm.entity.schema

import com.parinherm.entity.Person
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertSelectStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object PersonMapper : IMapper<Person> {

    override fun save(item: Person) {
        transaction {
            if (item.id == 0L) {
                val id = Persons.insertAndGetId {
                    mapItem(item, it)
               }
                item.id = id.value
            } else {
                Persons.update ({Persons.id eq item.id}) {
                    mapItem(item, it)
               }
            }
        }
    }

      fun mapItem(item: Person, statement: UpdateBuilder<Int>) {
        statement[Persons.name] = item.name
        statement[Persons.income] = item.income
        statement[Persons.age] = item.age
        statement[Persons.height] = item.height
        statement[Persons.deceased] = item.deceased
        statement[Persons.country] = item.country
        statement[Persons.enteredDate] = item.enteredDate
    }

    override fun getAll(keys: Map<String, Long>): List<Person> {
        val items: MutableList<Person> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = Persons.selectAll()
            query.orderBy(Persons.enteredDate to SortOrder.ASC)
            query.forEach {
                items.add(Person(it[Persons.id].value,
                    it[Persons.name],
                it[Persons.income],
                it[Persons.height],
                it[Persons.age],
                it[Persons.enteredDate],
                it[Persons.country],
                it[Persons.deceased]))
            }
        }
        return items
    }
}