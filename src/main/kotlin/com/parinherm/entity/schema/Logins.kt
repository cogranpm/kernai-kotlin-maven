package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object Logins : LongIdTable() {
    val name = varchar("name", 255)
    val category = varchar("category", LookupUtils.lookupFieldLength)
    val userName = varchar("userName", 2999)
    val password = varchar("password", 2500)
    val url = varchar("url", 2999)
    val notes = text("notes")
    val other = text("other")
}