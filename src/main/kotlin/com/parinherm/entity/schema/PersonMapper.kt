package com.parinherm.entity.schema

import com.parinherm.entity.Person
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object PersonMapper : IMapper<Person> {

    val table = Persons

    override fun save(item: Person) {
        MapperHelper.save(item, table, PersonMapper::mapItem)
    }

      fun mapItem(item: Person, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.income] = item.income
        statement[table.age] = item.age
        statement[table.height] = item.height
        statement[table.deceased] = item.deceased
        statement[table.country] = item.country
        statement[table.enteredDate] = item.enteredDate
    }

    override fun getAll(keys: Map<String, Long>): List<Person> {
        return MapperHelper.getAll(keys, table, null, table.enteredDate to SortOrder.ASC) {
            Person(it[table.id].value,
                it[table.name],
                it[table.income],
                it[table.height],
                it[table.age],
                it[table.enteredDate],
                it[table.country],
                it[table.deceased])
        }
    }
}