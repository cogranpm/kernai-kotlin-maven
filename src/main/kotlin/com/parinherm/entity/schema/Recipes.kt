package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object Recipes : LongIdTable() {
    val name = varchar("name", 255)
    val method = text("method")
    val category = varchar("category", LookupUtils.lookupFieldLength)
}