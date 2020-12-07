package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object SubjectMapper : IMapper<Subject> {

    val table = Subjects

    override fun save(item: Subject) {
        MapperHelper.save(item, table, SubjectMapper::mapItem)
   }

    private fun mapItem(item: Subject, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.comments] = item.comments
       statement[table.createdDate] = item.createdDate
       
       statement[table.shelfId] = item.shelfId
       
    }

    override fun getAll(keys: Map<String, Long>): List<Subject> {
    return MapperHelper.getAll(keys,
    table,
     table.shelfId eq keys["shelfId"] as Long 
    ,  table.title to SortOrder.ASC   ) {
            Subject(it[table.id].value,
                it[table.shelfId],
it[table.title], 
                it[table.comments]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    
    fun delete(parent: Shelf) {
        MapperHelper.delete(table, table.shelfId eq parent.id)
    }
    

    override fun delete(item: Subject) {
        PublicationMapper.delete(item)
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}