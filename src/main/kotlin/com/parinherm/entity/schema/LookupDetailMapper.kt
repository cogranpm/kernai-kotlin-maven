package com.parinherm.entity.schema

import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.Person
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

object LookupDetailMapper: IMapper<LookupDetail> {

   val table = LookupDetails

    override fun save(item: LookupDetail) {
        TODO("Not yet implemented")
    }

    override fun getAll(keys: Map<String, Long>): List<LookupDetail> {
        TODO("Not yet implemented")
    }

    override fun delete(item: LookupDetail) {
        TODO("Not yet implemented")
    }

    fun delete(parent: Lookup) {
        MapperHelper.delete(table, table.lookupId eq parent.id)
    }

}