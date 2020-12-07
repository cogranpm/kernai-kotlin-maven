package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object NoteSegmentTypeMapper : IMapper<NoteSegmentType> {

    val table = NoteSegmentTypes

    override fun save(item: NoteSegmentType) {
        MapperHelper.save(item, table, NoteSegmentTypeMapper::mapItem)
   }

    private fun mapItem(item: NoteSegmentType, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.fontDesc] = item.fontDesc
       statement[table.createdDate] = item.createdDate
       
       statement[table.noteSegmentTypeHeaderId] = item.noteSegmentTypeHeaderId
       
    }

    override fun getAll(keys: Map<String, Long>): List<NoteSegmentType> {
    return MapperHelper.getAll(keys,
    table,
     table.noteSegmentTypeHeaderId eq keys["noteSegmentTypeHeaderId"] as Long 
    ,  table.title to SortOrder.ASC   ) {
            NoteSegmentType(it[table.id].value,
                it[table.noteSegmentTypeHeaderId],
it[table.title]?:"", 
                it[table.fontDesc]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    
    fun delete(parent: NoteSegmentTypeHeader) {
        MapperHelper.delete(table, table.noteSegmentTypeHeaderId eq parent.id)
    }
    

    override fun delete(item: NoteSegmentType) {
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}