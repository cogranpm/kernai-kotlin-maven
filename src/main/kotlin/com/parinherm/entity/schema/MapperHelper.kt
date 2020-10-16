package com.parinherm.entity.schema

import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.NoteHeader
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction

object MapperHelper {

    fun <T> save(
        item: T,
        table: LongIdTable,
        fnMap: (entity: T, statement: UpdateBuilder<Int>) -> Unit
    ): Unit where T : IBeanDataEntity {
        transaction {
            addLogger(StdOutSqlLogger)
            if (item.id == 0L) {
                val id = table.insertAndGetId {
                    fnMap(item, it)
                }
                item.id = id.value
            } else {
                table.update({ table.id eq item.id }) {
                    fnMap(item, it)
                }
            }
        }
    }

    fun <T> getAll(
        keys: Map<String, Long>,
        table: LongIdTable,
        whereClause: Op<Boolean>?,
        orderBy: Pair<Expression<*>, SortOrder>,
        entityMaker: (ResultRow) -> T
    ): List<T> where T : IBeanDataEntity {
        val list: MutableList<T> = mutableListOf()
        transaction {
            val query: Query = if (whereClause != null) {
                table.select { whereClause }
            } else {
                table.selectAll()
            }
            query.orderBy(orderBy)
            query.forEach {
                list.add(
                    entityMaker(it)
                )
            }
        }
        return list
    }

    fun delete(table: LongIdTable, whereClause: Op<Boolean>) {
        transaction {
            table.deleteWhere {  whereClause }
        }
    }


}