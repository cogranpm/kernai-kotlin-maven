package com.parinherm.entity.schema

import com.parinherm.ApplicationData

import com.parinherm.entity.Menu

import com.parinherm.entity.MenuItem
import com.parinherm.lookups.LookupUtils
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object MenuItemMapper : IMapper<MenuItem> {

    val table = MenuItems

    override fun save(item: MenuItem) {
        MapperHelper.save(item, table, MenuItemMapper::mapItem)
    }

    private fun mapItem(item: MenuItem, statement: UpdateBuilder<Int>) {
        statement[table.text] = item.text
        statement[table.tabCaption] = item.tabCaption
        statement[table.modifierKey] = item.modifierKey
        statement[table.acceleratorKey] = item.acceleratorKey
        statement[table.viewId] = item.viewId
        statement[table.scriptPath] = item.scriptPath
        statement[table.font] = item.font
        statement[table.image] = item.image
        statement[table.menuId] = item.menuId
    }

    override fun getAll(keys: Map<String, Long>): List<MenuItem> {
        //todo, more defensive coding  here is needed in case lookups go missing
        return MapperHelper.getAll(
            keys,
            table,
            table.menuId eq keys["menuId"] as Long, table.text to SortOrder.ASC
        ) {
            MenuItem(
                it[table.id].value,
                it[table.menuId],
                it[table.text],
                it[table.tabCaption],
                it[table.modifierKey] ?: LookupUtils.getLookupByKey(LookupUtils.modifierKeyLookupKey, true)[0].code,
                it[table.acceleratorKey] ?: "",
                it[table.viewId] ?: "",
                it[table.scriptPath] ?: "",
                it[table.font] ?: LookupUtils.getLookupByKey(LookupUtils.fontLookupKey, true)[0].code,
                it[table.image] ?: LookupUtils.getLookupByKey(LookupUtils.imageLookupKey, true)[0].code
            )
        }
    }

    fun delete(parent: Menu) {
        MapperHelper.delete(table, table.menuId eq parent.id)
    }

    override fun delete(item: MenuItem) {

        MapperHelper.delete(table, table.id eq item.id)
    }
}
