package com.parinherm.entity.schema

import com.parinherm.entity.schema.PersonDetails.references
import org.jetbrains.exposed.dao.id.LongIdTable

object LookupDetails  : LongIdTable() {
    val code = varchar("code", 25)
    val label = varchar("label", 256)
    val lookupId = long("lookupId").references(Lookups.id)
}