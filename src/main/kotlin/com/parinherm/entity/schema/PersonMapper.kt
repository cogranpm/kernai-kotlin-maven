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
                Persons.update ({Persons.id eq item.id}) {
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