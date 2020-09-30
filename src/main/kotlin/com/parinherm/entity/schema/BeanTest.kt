package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object BeanTest: LongIdTable() {
    val name = varchar("name", 255)

}