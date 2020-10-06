package com.parinherm.entity.schema

import com.parinherm.entity.PersonDetail
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object PersonDetailMapper : IMapper<PersonDetail> {

    override fun save(item: PersonDetail) {
        transaction {
            if (item.id == 0L) {
                val id = PersonDetails.insertAndGetId {
                    it[nickname] = item.nickname
                    it[PersonDetails.beanTestId] = item.beanTestId
                    it[petSpecies] = item.petSpecies
               }
                item.id = id.value
            } else {
                PersonDetails.update ({PersonDetails.id eq item.id}) {
                    it[nickname] = item.nickname
                    it[PersonDetails.beanTestId] = item.beanTestId
                    it[petSpecies] = item.petSpecies
               }
            }
        }
    }

    override fun getAll(keys: Map<String, Long>): List<PersonDetail> {
        val list: MutableList<PersonDetail> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = PersonDetails.select {PersonDetails.beanTestId eq keys["beanTestId"] as Long}
            query.orderBy(PersonDetails.nickname to SortOrder.ASC)
            query.forEach {
                list.add(
                    PersonDetail(it[PersonDetails.id].value,
                    it[PersonDetails.nickname],
                    it[PersonDetails.beanTestId],
                    it[PersonDetails.petSpecies])
                )
            }
        }
        return list
    }
}