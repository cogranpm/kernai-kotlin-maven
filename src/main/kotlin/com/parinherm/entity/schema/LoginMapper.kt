package com.parinherm.entity.schema

import com.parinherm.entity.Login
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object LoginMapper : IMapper<Login> {
    override fun save(item: Login) {
        transaction {
            addLogger(StdOutSqlLogger)
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

    }

    private fun mapItem(item: Login, statement: UpdateBuilder<Int>) {
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
            addLogger(StdOutSqlLogger)
            val query: Query = Logins.selectAll()
            query.orderBy(Logins.name)
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