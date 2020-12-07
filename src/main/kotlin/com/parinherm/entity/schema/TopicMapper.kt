package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object TopicMapper : IMapper<Topic> {

    val table = Topics

    override fun save(item: Topic) {
        MapperHelper.save(item, table, TopicMapper::mapItem)
   }

    private fun mapItem(item: Topic, statement: UpdateBuilder<Int>) {
       statement[table.name] = item.name
       statement[table.comments] = item.comments
       statement[table.createdDate] = item.createdDate
       
       statement[table.publicationId] = item.publicationId
       
    }

    override fun getAll(keys: Map<String, Long>): List<Topic> {
    return MapperHelper.getAll(keys,
    table,
     table.publicationId eq keys["publicationId"] as Long 
    ,  table.name to SortOrder.ASC   ) {
            Topic(it[table.id].value,
                it[table.publicationId],
it[table.name], 
                it[table.comments]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    
    fun delete(parent: Publication) {
        MapperHelper.delete(table, table.publicationId eq parent.id)
    }
    

    override fun delete(item: Topic) {
        NoteMapper.delete(item)
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}