package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.select
import java.time.LocalDate
import java.time.LocalDateTime
import org.jetbrains.exposed.sql.transactions.transaction


object FieldDefinitionMapper : IMapper<FieldDefinition> {

    val table = FieldDefinitions

    override fun save(item: FieldDefinition) {
        MapperHelper.save(item, table, FieldDefinitionMapper::mapItem)
    }

    private fun mapItem(item: FieldDefinition, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.title] = item.title
        statement[table.required] = item.required
        statement[table.size] = item.size
        statement[table.dataType] = item.dataType
        statement[table.lookupKey] = item.lookupKey
        statement[table.viewDefinitionId] = item.viewDefinitionId
        statement[table.filterable] = item.filterable
        statement[table.default] = item.default
        statement[table.config] = item.config
        statement[table.sequence] = item.sequence
        statement[table.length] = item.length
        statement[table.referenceViewId] = item.referenceViewId
    }

    override fun getAll(keys: Map<String, Long>): List<FieldDefinition> {
        return MapperHelper.getAll(
            keys,
            table,
            table.viewDefinitionId eq keys["viewDefinitionId"] as Long, null
        ) {
            FieldDefinition(
                it[table.id].value,
                it[table.viewDefinitionId],
                it[table.name],
                it[table.title],
                it[table.required],
                it[table.size],
                it[table.dataType],
                it[table.lookupKey],
                it[table.filterable],
                it[table.default] ?: "",
                it[table.config] ?: "",
                it[table.sequence] ?: 0,
                it[table.length] ?: 0,
                it[table.referenceViewId] ?: 0
            )
        }
    }


    fun delete(parent: ViewDefinition) {
        MapperHelper.delete(table, table.viewDefinitionId eq parent.id)
    }


    override fun delete(item: FieldDefinition) {

        MapperHelper.delete(table, table.id eq item.id)
    }

    fun getNextSequence(viewDefinitionId: Long) : Int
    {
        var maxNum = 10;
            transaction {

                val max =
                    table.slice(table.sequence.max()).select(table.viewDefinitionId eq viewDefinitionId).firstOrNull()
                val amt = max?.getOrNull(table.sequence.max())
                if(amt != null){
                    maxNum = amt + 10
                }
            }
        return maxNum
    }
}