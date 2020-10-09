package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.`java-time`.date

object Persons: LongIdTable() {
    val name = varchar("name", 255)
    val income = decimal("income", 10, 2)
    val height = double("height")
    val age = integer("age")
    val country = varchar("country", 60)
    val deceased = bool("deceased")
    val enteredDate = date("enteredDate")
}