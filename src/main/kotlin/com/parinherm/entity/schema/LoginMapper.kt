package com.parinherm.entity.schema

import com.parinherm.entity.Login
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object LoginMapper : IMapper<Login> {


    override fun save(item: Login) : Unit {
        MapperHelper.save(item, Logins, LoginMapper::mapItem)
        /*
        transaction {
            if (item.id == 0L) {
                val id = Logins.insertAndGetId {
                    mapItem(item, it)
                }
                item.id = id.value
            } else {
                Logins.update({ Logins.id eq item.id }) {
                    mapItem(item, it)
                }
            }
        }
         */

    }

    fun mapItem(item: Login, statement: UpdateBuilder<Int>) : Unit {
        statement[Logins.name] = item.name
        statement[Logins.category] = item.category
        statement[Logins.userName] = item.userName
        statement[Logins.password] = item.password
        statement[Logins.url] = item.url
        statement[Logins.notes] = item.notes
        statement[Logins.other] = item.other
    }


    override fun getAll(keys: Map<String, Long>): List<Login> {
        val items: MutableList<Login> = mutableListOf()
        transaction {
            val query: Query = Logins.selectAll()
            query.orderBy(Logins.name to SortOrder.DESC)
            query.forEach {
                items.add(
                    Login(
                        it[Logins.id].value,
                        it[Logins.name],
                        it[Logins.category],
                        it[Logins.userName],
                        it[Logins.password],
                        it[Logins.url],
                        it[Logins.notes],
                        it[Logins.other]
                    )
                )
            }
        }
        return items
    }
}