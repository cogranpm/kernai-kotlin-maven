package com.parinherm.entity.schema

import com.parinherm.entity.SalesforceConfig
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder


object SalesforceConfigMapper  : IMapper<SalesforceConfig> {
   val table = SalesforceConfigs

    override fun save(item: SalesforceConfig) {
        MapperHelper.save(item, table, SalesforceConfigMapper::mapItem)
    }

    fun mapItem(item: SalesforceConfig, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.salesforceUrl] = item.salesforceUrl
        statement[table.loginToken] = item.loginToken
    }

    override fun getAll(keys: Map<String, Long>): List<SalesforceConfig> {
        return MapperHelper.getAll(keys, table, null, table.name to SortOrder.ASC) {
            SalesforceConfig(it[table.id].value,
                it[table.name],
                it[table.salesforceUrl],
                it[table.loginToken])
        }
    }

    override fun delete(item: SalesforceConfig) {
        MapperHelper.delete(table, table.id eq item.id)
    }

}