package com.parinherm.entity.schema

import com.parinherm.entity.Notebook
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object NotebookMapper : IMapper<Notebook> {
    override fun save(item: Notebook) {
        MapperHelper.save(item, Notebooks, NotebookMapper::mapItem)
        /*
        transaction {
            addLogger(StdOutSqlLogger)
            if (item.id == 0L) {
                val id = Notebooks.insertAndGetId {
                    mapItem(item, it)
                }
                item.id = id.value
            } else {
                Notebooks.update({ Notebooks.id eq item.id }) {
                    mapItem(item, it)
                }
            }
        }

         */
    }

    private fun mapItem(item: Notebook, statement: UpdateBuilder<Int>) {
        statement[Notebooks.name] = item.name
        statement[Notebooks.comments] = item.comments
    }

    override fun getAll(keys: Map<String, Long>): List<Notebook> {
        val items: MutableList<Notebook> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query = Notebooks.selectAll()
            query.orderBy(Notebooks.name to SortOrder.ASC)
            query.forEach {
                items.add(
                    Notebook(
                        it[Notebooks.id].value,
                        it[Notebooks.name],
                        it[Notebooks.comments]
                    )
                )
            }
        }
        return items
    }

}