package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object Lookups  : LongIdTable() {
    val key = varchar("key", 25)
    val label = varchar("label", 256)
    val encrypted = bool("encrypted")
}