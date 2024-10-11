package com.parinherm.entity.schema

import org.jetbrains.exposed.dao.id.LongIdTable

object SalesforceConfigs : LongIdTable()
{
    //eg Dev Test or Production
    val name = varchar("name", 255)
    val salesforceUrl = varchar("salesforceUrl", 2000)
    val loginToken = varchar("loginToken", 2000);
}