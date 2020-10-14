package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable

object Snippets : LongIdTable() {
    val name = varchar("name", 255)
    val language = varchar("language", ApplicationData.lookupFieldLength)
    val category = varchar("category", ApplicationData.lookupFieldLength)
    val topic = varchar("topic", ApplicationData.lookupFieldLength)
    val type = varchar("type", ApplicationData.lookupFieldLength)
    val desc = varchar("desc", 4000)
    val body = text("body")
}
