package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object NoteSegmentTypeHeaders: LongIdTable() {
val title = varchar("title", 255)
    val helpText = text("helpText").nullable()
    val createdDate = date("createdDate")
    
    
}