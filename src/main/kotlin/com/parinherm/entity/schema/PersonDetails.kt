package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object PersonDetails : LongIdTable() {
    // the id column is presumed
    val nickname = varchar("nickname", 255)
    val petSpecies = varchar("petSpecies", 10)
    val beanTestId = long("beanTestId").references(BeanTests.id)
    // also presumed
    //override val primaryKey = PrimaryKey(id, name="PK_PersonDetail_Id")
}