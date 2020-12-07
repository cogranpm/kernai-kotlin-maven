package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object Topics: LongIdTable() {
val name = varchar("name", 255)
    val comments = text("comments").nullable()
    val createdDate = date("createdDate")
    
     val publicationId = long("publicationId").references(Publications.id)
    
}