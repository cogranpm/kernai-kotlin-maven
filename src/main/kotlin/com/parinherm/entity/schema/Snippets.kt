package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object Snippets : LongIdTable() {
    val name = varchar("name", 255)
    val language = varchar("language", LookupUtils.lookupFieldLength)
    val category = varchar("category", LookupUtils.lookupFieldLength)
    val topic = varchar("topic", LookupUtils.lookupFieldLength)
    val type = varchar("type", LookupUtils.lookupFieldLength)
    val desc = varchar("desc", 4000)
    val body = text("body")

}
