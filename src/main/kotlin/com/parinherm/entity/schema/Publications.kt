package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object Publications: LongIdTable() {
val title = varchar("title", 255)
    val type = varchar("type", ApplicationData.lookupFieldLength).nullable()
    val comments = text("comments").nullable()
    val createdDate = date("createdDate")
    
     val subjectId = long("subjectId").references(Subjects.id)
    
}