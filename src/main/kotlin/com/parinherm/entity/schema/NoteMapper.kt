package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate

object NoteMapper : IMapper<Note> {

    val table = Notes

    override fun save(item: Note) {
        MapperHelper.save(item, table, NoteMapper::mapItem)
   }

    private fun mapItem(item: Note, statement: UpdateBuilder<Int>) {
       statement[table.title] = item.title
       statement[table.description] = item.description
       statement[table.titleAudioFile] = item.titleAudioFile
       statement[table.descriptionAudioFile] = item.descriptionAudioFile
       statement[table.createdDate] = item.createdDate
       
       statement[table.topicId] = item.topicId
       
    }

    override fun getAll(keys: Map<String, Long>): List<Note> {
    return MapperHelper.getAll(keys,
    table,
     table.topicId eq keys["topicId"] as Long 
    ,  table.title to SortOrder.ASC     ) {
            Note(it[table.id].value,
                it[table.topicId],
it[table.title], 
                it[table.description]?:"", 
                it[table.titleAudioFile]?:"", 
                it[table.descriptionAudioFile]?:"", 
                it[table.createdDate]
                
            )
        }
    }

    
    fun delete(parent: Topic) {
        MapperHelper.delete(table, table.topicId eq parent.id)
    }
    

    override fun delete(item: Note) {
        
        MapperHelper.delete(table, table.id eq item.id)
    }
}