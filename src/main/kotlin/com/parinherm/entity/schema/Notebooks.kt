package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object Notebooks  : LongIdTable() {
    val name = varchar("name", 255)
    val comments = text("comments")
}