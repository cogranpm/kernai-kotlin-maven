package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable

object Logins : LongIdTable() {
    val name = varchar("name", 255)
    val category = varchar("category", ApplicationData.lookupFieldLength)
    val userName = varchar("userName", 2999)
    val password = varchar("password", ApplicationData.lookupFieldLength)
    val url = varchar("url", 2999)
    val notes = text("notes")
    val other = text("other")
}