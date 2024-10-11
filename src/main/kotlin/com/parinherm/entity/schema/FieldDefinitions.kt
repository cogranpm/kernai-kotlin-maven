package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.ViewDefinitions.nullable
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object FieldDefinitions: LongIdTable() {
    val name = varchar("name", 255)
    val title = varchar("title", 255)
    val required = bool("required").default(false)
    val size = varchar("size", LookupUtils.lookupFieldLength)
    val dataType = varchar("dataType", LookupUtils.lookupFieldLength)
    val lookupKey = varchar("lookupKey", LookupUtils.lookupFieldLength)
    val viewDefinitionId = long("viewDefinitionId").references(ViewDefinitions.id)
    val filterable = bool("filterable").default(false)
    val default = varchar("default", 255).nullable().default("")
    val config = text("config").nullable() //is a long text field that takes json
}