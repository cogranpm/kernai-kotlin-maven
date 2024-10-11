package com.parinherm.entity.schema

import com.parinherm.entity.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import com.parinherm.ApplicationData
import java.time.LocalDate
import java.time.LocalDateTime

object ViewDefinitionMapper : IMapper<ViewDefinition> {

    val table = ViewDefinitions

    override fun save(item: ViewDefinition) {
        MapperHelper.save(item, table, ViewDefinitionMapper::mapItem)
   }

    private fun mapItem(item: ViewDefinition, statement: UpdateBuilder<Int>) {
       statement[table.viewId] = item.viewId
       statement[table.title] = item.title
       statement[table.listWeight] = item.listWeight
       statement[table.editWeight] = item.editWeight
       statement[table.sashOrientation] = item.sashOrientation
       statement[table.entityName] = item.entityName
       statement[table.parentId] = item.parentId
        statement[table.config] = item.config
    }

    override fun getAll(keys: Map<String, Long>): List<ViewDefinition> {
    return MapperHelper.getAll(keys,
    table,
    table.parentId eq 0
    ,  table.viewId to SortOrder.ASC      ) {
            ViewDefinition(it[table.id].value,
                it[table.parentId],
                it[table.viewId],
                it[table.title], 
                it[table.listWeight], 
                it[table.editWeight], 
                it[table.sashOrientation], 
                it[table.entityName],
                it[table.config]?:""
            )
        }
    }

    fun getById(id: Long): ViewDefinition {
         var list = MapperHelper.getAll(mapOf("" to 0),
            table,
            table.id eq id, null
            ) {
            ViewDefinition(it[table.id].value,
                it[table.parentId],
                it[table.viewId],
                it[table.title],
                it[table.listWeight],
                it[table.editWeight],
                it[table.sashOrientation],
                it[table.entityName],
                it[table.config]?:""
            )
        }
        return list.first()
    }


    fun getAllByParent(keys: Map<String, Long>): List<ViewDefinition> {
        return MapperHelper.getAll(keys,
            table,
            table.parentId eq keys["viewDefinitionId"] as Long
            ,  table.viewId to SortOrder.ASC      ) {
            ViewDefinition(it[table.id].value,
                it[table.parentId],
                it[table.viewId],
                it[table.title],
                it[table.listWeight],
                it[table.editWeight],
                it[table.sashOrientation],
                it[table.entityName],
                it[table.config]?:""
            )
        }
    }


    override fun delete(item: ViewDefinition) {
        FieldDefinitionMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun getByViewId(key: String): List<ViewDefinition> {
        return MapperHelper.getAll(
            mapOf("" to 0),
            table, table.viewId eq key, table.viewId to SortOrder.ASC
        ) {
            ViewDefinition(
                it[table.id].value,
                it[table.parentId],
                it[table.viewId],
                it[table.title],
                it[table.listWeight],
                it[table.editWeight],
                it[table.sashOrientation],
                it[table.entityName],
                it[table.config]?:""
            )
        }
    }

    fun createDefault(viewDefinition: ViewDefinition, fieldDefinitions: List<FieldDefinition>) {
        val existing = getByViewId(viewDefinition.viewId)
        if (existing.isEmpty()) {
            save(viewDefinition)
            fieldDefinitions.forEach {
                it.viewDefinitionId = viewDefinition.id
                FieldDefinitionMapper.save(it)
            }
        }
    }
}