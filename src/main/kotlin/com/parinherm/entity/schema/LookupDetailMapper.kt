package com.parinherm.entity.schema

import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object LookupDetailMapper: IMapper<LookupDetail> {

   val table = LookupDetails


    fun mapItem(item: LookupDetail, statement: UpdateBuilder<Int>) : Unit {
        statement[table.code] = item.code
        statement[table.label] = item.label
        statement[table.lookupId] = item.lookupId
    }

    override fun save(item: LookupDetail) {
        MapperHelper.save(item, table, ::mapItem)
    }

    override fun getAll(keys: Map<String, Long>): List<LookupDetail> {
        return MapperHelper.getAll(keys, table, null, table.label to SortOrder.ASC) {
            LookupDetail(
                    it[table.id].value,
                    it[table.lookupId],
                    it[table.code],
                    it[table.label])
        }
    }

    override fun delete(item: LookupDetail) {
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun delete(parent: Lookup) {
        MapperHelper.delete(table, table.lookupId eq parent.id)
    }

}