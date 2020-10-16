package com.parinherm.entity.schema

import com.parinherm.entity.schema.NoteHeaders.references
import org.jetbrains.exposed.dao.id.LongIdTable

object NoteDetails : LongIdTable(){
    val name = varchar("name", 255)
    val body = text("body")
    val sourceCode = text("sourceCode")
    val comments = text("comments")
    val noteHeaderId = long("noteHeaderId").references(NoteHeaders.id)
}