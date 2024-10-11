package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import org.jetbrains.exposed.dao.id.LongIdTable

object AppVersions: LongIdTable() {
val version = double("version")
    
    
}
