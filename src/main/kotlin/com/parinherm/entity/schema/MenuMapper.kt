package com.parinherm.entity.schema

import com.parinherm.entity.Menu
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object MenuMapper : IMapper<Menu> {

    val table = Menus

    override fun save(item: Menu) {
        MapperHelper.save(item, table, MenuMapper::mapItem)
    }

    private fun mapItem(item: Menu, statement: UpdateBuilder<Int>) {
        statement[table.text] = item.text


    }

    override fun getAll(keys: Map<String, Long>): List<Menu> {
        return MapperHelper.getAll(
            keys,
            table,
            null, table.text to SortOrder.ASC
        ) {
            Menu(
                it[table.id].value,
                it[table.text]
            )
        }
    }


    override fun delete(item: Menu) {
        MenuItemMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }
}
