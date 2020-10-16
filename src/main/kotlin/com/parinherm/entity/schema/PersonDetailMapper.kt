package com.parinherm.entity.schema

import com.parinherm.entity.Person
import com.parinherm.entity.PersonDetail
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object PersonDetailMapper : IMapper<PersonDetail> {

    val table = PersonDetails

    override fun save(item: PersonDetail) {
        MapperHelper.save(item, table, PersonDetailMapper::mapItem)
    }

    fun mapItem(item: PersonDetail, statement: UpdateBuilder<Int>) {
        statement[table.nickname] = item.nickname
        statement[table.personId] = item.personId
        statement[table.petSpecies] = item.petSpecies
    }


    override fun getAll(keys: Map<String, Long>): List<PersonDetail> {
        return MapperHelper.getAll(keys, table, table.personId eq keys["personId"] as Long, table.nickname to SortOrder.ASC ) {
            PersonDetail(it[table.id].value,
                it[table.nickname],
                it[table.personId],
                it[table.petSpecies])
        }
    }

    fun delete(parent: Person) {
        MapperHelper.delete(table, table.personId eq parent.id)
    }

    override fun delete(item: PersonDetail) {
        MapperHelper.delete(table, table.id eq item.id)
    }
}