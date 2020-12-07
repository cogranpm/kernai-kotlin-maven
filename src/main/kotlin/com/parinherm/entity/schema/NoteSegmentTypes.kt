package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object NoteSegmentTypes: LongIdTable() {
val title = varchar("title", 255).nullable()
    val fontDesc = varchar("fontDesc", 255).nullable()
    val createdDate = date("createdDate")
    
     val noteSegmentTypeHeaderId = long("noteSegmentTypeHeaderId").references(NoteSegmentTypeHeaders.id)
    
}