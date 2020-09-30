package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object PersonDetail : LongIdTable() {
    // the id column is presumed
    val nickname = varchar("nickname", 255)
    val petSpecies = varchar("petSpecies", 10)
    val beanTestId = reference("beanTestId", BeanTest)
    // also presumed
    //override val primaryKey = PrimaryKey(id, name="PK_PersonDetail_Id")
}