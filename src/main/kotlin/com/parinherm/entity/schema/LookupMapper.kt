package com.parinherm.entity.schema

import com.parinherm.entity.Lookup
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


object LookupMapper : IMapper<Lookup> {

    val table = Lookups

    fun mapItem(item: Lookup, statement: UpdateBuilder<Int>) : Unit {
        statement[table.key] = item.key
        statement[table.label] = item.label
   }

   fun getLookups() {
       transaction {
           addLogger(StdOutSqlLogger)
           val query = table.join(LookupDetails, JoinType.INNER)
               .slice(table.key, LookupDetails.code, LookupDetails.label)
               .selectAll()
           query.orderBy(table.key to SortOrder.ASC)
           query.forEach {
               println(it)
           }
       }
   }

    override fun save(item: Lookup) {
        MapperHelper.save(item, table, ::mapItem)
    }

    override fun getAll(keys: Map<String, Long>): List<Lookup> {
        return MapperHelper.getAll(keys, table, null, table.label to SortOrder.ASC) {
            Lookup(
                it[table.id].value,
                it[table.key],
                it[table.label])
        }
    }

    override fun delete(item: Lookup) {
        LookupDetailMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }
}