package com.parinherm.entity.schema

import com.parinherm.entity.schema.ViewDefinitions.nullable
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object AssociationDefinitions  : LongIdTable() {
    val name = varchar("name", 255)
    val owningEntity = long("owningEntity")
    val ownedEntity = long("ownedEntity")
    val junctionEntityName = varchar("junctionEntityName", 255)
    val owningType = varchar("owningType", LookupUtils.lookupFieldLength)
    val ownedType = varchar("ownedType", LookupUtils.lookupFieldLength)
    val config = text("config").nullable() //is a long text field that takes json
}
