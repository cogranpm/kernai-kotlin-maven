package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.dao.id.LongIdTable

object MenuItems: LongIdTable() {
val text = varchar("text", 255)
    val tabCaption = varchar("tabCaption", 255)
    val modifierKey = varchar("modifierKey", LookupUtils.lookupFieldLength).nullable()
    val acceleratorKey = varchar("acceleratorKey", 255).nullable()
    val viewId = varchar("viewId", 255).nullable()
    val scriptPath = varchar("scriptPath", 3999).nullable()
    val font = varchar("font", LookupUtils.lookupFieldLength).nullable()
    val image = varchar("image", LookupUtils.lookupFieldLength).nullable()
    
     val menuId = long("menuId").references(Menus.id)
    
}
