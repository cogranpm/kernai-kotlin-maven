package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.schema.Notes.nullable
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime

object ViewDefinitions: LongIdTable() {
    val parentId = long("parentId")
    val viewId = varchar("viewId", 255)
    val title = varchar("title", 255)
    val listWeight = integer("listWeight")
    val editWeight = integer("editWeight")
    val sashOrientation = varchar("sashOrientation", LookupUtils.lookupFieldLength)
    val entityName = varchar("entityName", 255)
    val config = text("config").nullable() //is a long text field that takes json
}