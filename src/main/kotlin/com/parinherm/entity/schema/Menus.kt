package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable

object Menus: LongIdTable() {
val text = varchar("text", 255)
    
    
}
