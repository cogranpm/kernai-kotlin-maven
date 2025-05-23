package com.parinherm.entity.schema

import com.parinherm.entity.Login
import com.parinherm.security.Cryptographer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object LoginMapper : IMapper<Login> {

    val table = Logins

    override fun save(item: Login) : Unit {
        val oldPassword = item.password
        item.password = Cryptographer.encrypt(item.password)
        MapperHelper.save(item, table, LoginMapper::mapItem)
        item.password = oldPassword
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
                Cryptographer.decrypt(it[table.password]),
                it[table.url],
                it[table.notes],
                it[table.other])
        }
    }

    override fun delete(item: Login) {
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun encryptAll(){
        val logins = getAll(mapOf())
        val encryptedLogins = logins.map {
            Login(it.id, it.name, it.category, it.userName, Cryptographer.encrypt(it.password), it.url, it.notes, it.other)
        }
        encryptedLogins.forEach {
            save(it)
        }
    }
}