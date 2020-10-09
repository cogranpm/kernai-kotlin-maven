package com.parinherm.entity.schema

import com.parinherm.entity.Person
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object PersonMapper : IMapper<Person> {

    override fun save(item: Person) {
        transaction {
            if (item.id == 0L) {
                val id = Persons.insertAndGetId {
                    it[name] = item.name
                    it[income] = item.income
                    it[age] = item.age
                    it[height] = item.height
                    it[deceased] = item.deceased
                    it[country] = item.country
                    it[enteredDate] = item.enteredDate
                }
                item.id = id.value
            } else {
                Persons.update ({Person.id eq item.id}) {
                    it[name] = item.name
                    it[income] = item.income
                    it[age] = item.age
                    it[height] = item.height
                    it[deceased] = item.deceased
                    it[country] = item.country
                    it[enteredDate] = item.enteredDate
                }
            }
        }
    }

    override fun getAll(keys: Map<String, Long>): List<Person> {
        val items: MutableList<Person> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = Persons.selectAll()
            query.orderBy(Persons.enteredDate to SortOrder.ASC)
            query.forEach {
                items.add(Person(it[Person.id].value,
                    it[Person.name],
                it[Person.income],
                it[Person.height],
                it[Person.age],
                it[Person.enteredDate],
                it[Person.country],
                it[Person.deceased]))
            }
        }
        return items
    }
}