package com.parinherm.entity.schema

import com.parinherm.entity.PersonDetail
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object PersonDetailMapper : IMapper<PersonDetail> {

    override fun save(item: PersonDetail) {
        transaction {
            if (item.id == 0L) {
                val id = PersonDetails.insertAndGetId {
                mapItem(item, it)
               }
                item.id = id.value
            } else {
                PersonDetails.update ({PersonDetails.id eq item.id}) {
                    mapItem(item, it)
               }
            }
        }
    }

    fun mapItem(item: PersonDetail, statement: UpdateBuilder<Int>) {
        statement[PersonDetails.nickname] = item.nickname
        statement[PersonDetails.personId] = item.personId
        statement[PersonDetails.petSpecies] = item.petSpecies
    }


    override fun getAll(keys: Map<String, Long>): List<PersonDetail> {
        val list: MutableList<PersonDetail> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = PersonDetails.select {PersonDetails.personId eq keys["personId"] as Long}
            query.orderBy(PersonDetails.nickname to SortOrder.ASC)
            query.forEach {
                list.add(
                    PersonDetail(it[PersonDetails.id].value,
                    it[PersonDetails.nickname],
                    it[PersonDetails.personId],
                    it[PersonDetails.petSpecies])
                )
            }
        }
        return list
    }
}