package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object Notes: LongIdTable() {
val title = varchar("title", 255)
    val description = text("description").nullable()
    val titleAudioFile = varchar("titleAudioFile", 255).nullable()
    val descriptionAudioFile = varchar("descriptionAudioFile", 255).nullable()
    val createdDate = date("createdDate")
    
     val topicId = long("topicId").references(Topics.id)
    
}