package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date

object NoteSegments : LongIdTable() {
    val noteSegmentTypeId = long("noteSegmentTypeId").nullable()
    val title = varchar("title", 255)
    val body = text("body")
    val bodyFile = varchar("bodyFile", 255).nullable()
    val createdDate = date("createdDate").nullable()

    val noteId = long("noteId").references(Notes.id)

}