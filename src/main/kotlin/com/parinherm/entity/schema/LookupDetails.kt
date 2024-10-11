package com.parinherm.entity.schema

import com.parinherm.entity.schema.PersonDetails.references
import org.jetbrains.exposed.dao.id.LongIdTable

object LookupDetails  : LongIdTable() {
    /* this value might encrypted, so it is large
     */
    val code = varchar("code", 2500)
    val label = varchar("label", 256)
    val lookupId = long("lookupId").references(Lookups.id)
}