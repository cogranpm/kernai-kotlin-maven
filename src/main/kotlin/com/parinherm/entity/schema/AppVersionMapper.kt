package com.parinherm.entity.schema

import com.parinherm.ApplicationData

import com.parinherm.entity.AppVersion
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object AppVersionMapper : IMapper<AppVersion> {

    val table = AppVersions

    override fun save(item: AppVersion) {
        MapperHelper.save(item, table, AppVersionMapper::mapItem)
   }

    private fun mapItem(item: AppVersion, statement: UpdateBuilder<Int>) {
       statement[table.version] = item.version
       
       
    }

    override fun getAll(keys: Map<String, Long>): List<AppVersion> {
    return MapperHelper.getAll(keys,
    table,
     null 
    ,  table.version to SortOrder.ASC ) {
            AppVersion(it[table.id].value,
                
it[table.version]
                
            )
        }
    }

    

    override fun delete(item: AppVersion) {
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}
