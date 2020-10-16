package com.parinherm.entity.schema

import com.parinherm.entity.PersonDetail
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

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
}