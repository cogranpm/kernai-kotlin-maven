package com.parinherm.entity.schema

import com.parinherm.entity.Notebook
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object NotebookMapper : IMapper<Notebook> {

    val table = Notebooks

    override fun save(item: Notebook) {
        MapperHelper.save(item, table, NotebookMapper::mapItem)
    }

    private fun mapItem(item: Notebook, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.comments] = item.comments
    }

    override fun getAll(keys: Map<String, Long>): List<Notebook> {
       return MapperHelper.getAll(keys, table, null, table.name to SortOrder.ASC) {
           Notebook(
               it[table.id].value,
               it[table.name],
               it[table.comments]
           )
       }

    }

    override fun delete(item: Notebook) {
        NoteHeaderMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }

}