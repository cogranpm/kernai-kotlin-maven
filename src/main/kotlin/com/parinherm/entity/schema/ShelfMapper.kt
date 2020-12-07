package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object ShelfMapper : IMapper<Shelf> {

    val table = Shelfs

    override fun save(item: Shelf) {
        MapperHelper.save(item, table, ShelfMapper::mapItem)
   }

    private fun mapItem(item: Shelf, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.comments] = item.comments
       statement[table.createdDate] = item.createdDate
       
       
    }

    override fun getAll(keys: Map<String, Long>): List<Shelf> {
    return MapperHelper.getAll(keys,
    table,
     null 
    ,  table.title to SortOrder.ASC   ) {
            Shelf(it[table.id].value,
                
it[table.title], 
                it[table.comments]?:"", 
                it[table.createdDate]?:LocalDate.now()
                
            )
        }
    }

    

    override fun delete(item: Shelf) {
        SubjectMapper.delete(item)
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}