package com.parinherm.entity.schema

import com.parinherm.entity.Login
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object LoginMapper : IMapper<Login> {

    val table = Logins

    override fun save(item: Login) : Unit {
        MapperHelper.save(item, table, LoginMapper::mapItem)
    }

    fun mapItem(item: Login, statement: UpdateBuilder<Int>) : Unit {
        statement[table.name] = item.name
        statement[table.category] = item.category
        statement[table.userName] = item.userName
        statement[table.password] = item.password
        statement[table.url] = item.url
        statement[table.notes] = item.notes
        statement[table.other] = item.other
    }


    override fun getAll(keys: Map<String, Long>): List<Login> {
        return MapperHelper.getAll(keys, table, null, table.name to SortOrder.ASC) {
            Login(
                it[table.id].value,
                it[table.name],
                it[table.category],
                it[table.userName],
                it[table.password],
                it[table.url],
                it[table.notes],
                it[table.other])
        }
    }

    override fun delete(item: Login) {
        MapperHelper.delete(table, table.id eq item.id)
    }
}