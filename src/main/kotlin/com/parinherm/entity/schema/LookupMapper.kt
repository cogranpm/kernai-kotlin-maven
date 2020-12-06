package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
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

   fun getLookups() : Map<String, List<LookupDetail>>{
       var lookups = mutableMapOf<String, List<LookupDetail>>()
       transaction {
           val query = table.join(LookupDetails, JoinType.INNER)
               .slice(table.key, LookupDetails.id, LookupDetails.lookupId, LookupDetails.code, LookupDetails.label)
               .selectAll()
           query.orderBy(table.key to SortOrder.ASC)
           lookups = query.map {
               Pair(it[table.key], LookupDetail(
                   it[LookupDetails.id].value,
                   it[LookupDetails.lookupId],
                   it[LookupDetails.code],
                   it[LookupDetails.label]))
           }.groupBy(
               {it.first},
               {it.second}
           ) as MutableMap<String, List<LookupDetail>>
       }
       return lookups
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