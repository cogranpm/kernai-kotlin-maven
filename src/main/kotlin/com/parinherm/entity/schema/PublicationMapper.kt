package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object PublicationMapper : IMapper<Publication> {

    val table = Publications

    override fun save(item: Publication) {
        MapperHelper.save(item, table, PublicationMapper::mapItem)
   }

    private fun mapItem(item: Publication, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.type] = item.type
       statement[table.comments] = item.comments
       statement[table.createdDate] = item.createdDate
       
       statement[table.subjectId] = item.subjectId
       
    }

    override fun getAll(keys: Map<String, Long>): List<Publication> {
    return MapperHelper.getAll(keys,
    table,
     table.subjectId eq keys["subjectId"] as Long 
    ,  table.title to SortOrder.ASC    ) {
            Publication(it[table.id].value,
                it[table.subjectId],
it[table.title], 
                it[table.type]?:ApplicationData.publicationTypeList[0].code,
                it[table.comments]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    
    fun delete(parent: Subject) {
        MapperHelper.delete(table, table.subjectId eq parent.id)
    }
    

    override fun delete(item: Publication) {
        TopicMapper.delete(item)
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}