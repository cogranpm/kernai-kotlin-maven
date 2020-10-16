package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object NoteHeaders : LongIdTable() {
    val name = varchar("name", 255)
    val comments = text("comments")
    val notebookId = long("notebookId").references(Notebooks.id)
}