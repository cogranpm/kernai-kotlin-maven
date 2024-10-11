package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.date

object Publications: LongIdTable() {
val title = varchar("title", 255)
    val type = varchar("type", LookupUtils.lookupFieldLength).nullable()
    val comments = text("comments").nullable()
    val createdDate = date("createdDate")
    
     val subjectId = long("subjectId").references(Subjects.id)
    
}