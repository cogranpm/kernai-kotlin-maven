package com.parinherm.entity.schema

import com.parinherm.entity.Notebook
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

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
        val items: MutableList<Notebook> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = table.selectAll()
            query.orderBy(table.name to SortOrder.ASC)
            query.forEach {
                items.add(
                    Notebook(
                        it[table.id].value,
                        it[table.name],
                        it[table.comments]
                    )
                )
            }
        }
        return items
    }

}