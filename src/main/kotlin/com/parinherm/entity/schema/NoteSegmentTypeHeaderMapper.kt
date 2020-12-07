package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object NoteSegmentTypeHeaderMapper : IMapper<NoteSegmentTypeHeader> {

    val table = NoteSegmentTypeHeaders

    override fun save(item: NoteSegmentTypeHeader) {
        MapperHelper.save(item, table, NoteSegmentTypeHeaderMapper::mapItem)
   }

    private fun mapItem(item: NoteSegmentTypeHeader, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.helpText] = item.helpText
       statement[table.createdDate] = item.createdDate
       
       
    }

    override fun getAll(keys: Map<String, Long>): List<NoteSegmentTypeHeader> {
    return MapperHelper.getAll(keys,
    table,
     null 
    ,  table.title to SortOrder.ASC   ) {
            NoteSegmentTypeHeader(it[table.id].value,
                
it[table.title], 
                it[table.helpText]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    

    override fun delete(item: NoteSegmentTypeHeader) {
        NoteSegmentTypeMapper.delete(item)
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}