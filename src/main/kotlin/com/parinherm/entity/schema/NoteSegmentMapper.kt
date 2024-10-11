package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object NoteSegmentMapper : IMapper<NoteSegment> {

    val table = NoteSegments

    override fun save(item: NoteSegment) {
        MapperHelper.save(item, table, NoteSegmentMapper::mapItem)
   }

    private fun mapItem(item: NoteSegment, statement: UpdateBuilder<Int>) {
       statement[table.noteSegmentTypeId] = item.noteSegmentTypeId
       statement[table.title] = item.title
       statement[table.body] = item.body
       statement[table.bodyFile] = item.bodyFile
       statement[table.createdDate] = item.createdDate
       
       statement[table.noteId] = item.noteId
       
    }

    override fun getAll(keys: Map<String, Long>): List<NoteSegment> {
    return MapperHelper.getAll(keys,
    table,
     table.noteId eq keys["noteId"] as Long 
    ,  table.noteSegmentTypeId to SortOrder.ASC     ) {
            NoteSegment(it[table.id].value,
                it[table.noteId],
it[table.noteSegmentTypeId]?:0, 
                it[table.title], 
                it[table.body], 
                it[table.bodyFile]?:"", 
                it[table.createdDate]?:LocalDate.now()
                
            )
        }
    }

    
    fun delete(parent: Note) {
        MapperHelper.delete(table, table.noteId eq parent.id)
    }
    

    override fun delete(item: NoteSegment) {
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}