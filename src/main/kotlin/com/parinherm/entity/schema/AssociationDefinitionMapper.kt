package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.AssociationDefinition
import com.parinherm.entity.ViewDefinition
import com.parinherm.form.definitions.ViewDef
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


object AssociationDefinitionMapper : IMapper<AssociationDefinition> {
    val table = AssociationDefinitions

    fun mapItem(item: AssociationDefinition, statement: UpdateBuilder<Int>) {
        statement[table.name] = item.name
        statement[table.owningType] = item.owningType
        statement[table.ownedType] = item.ownedType
        statement[table.junctionEntityName] = item.junctionEntityName
        statement[table.owningEntity] = item.owningEntity
        statement[table.ownedEntity] = item.ownedEntity
        statement[table.config] = item.config
    }

    override fun save(item: AssociationDefinition) {
        MapperHelper.save(item, table, AssociationDefinitionMapper::mapItem)
    }

    override fun getAll(keys: Map<String, Long>): List<AssociationDefinition> {
        return MapperHelper.getAll(
            keys, table,
            null, table.name to SortOrder.ASC
        ) {
            AssociationDefinition(
                it[table.id].value,
                it[table.owningEntity],
                it[table.ownedEntity],
                it[table.name],
                it[table.junctionEntityName],
                it[table.owningType],
                it[table.ownedType],
                it[table.config] ?: ""
            )
        }
    }

    override fun delete(item: AssociationDefinition) {
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun getAllAsOwner(viewDef: ViewDef): List<AssociationDefinition> {
        val list: MutableList<AssociationDefinition> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query =
                table.select { table.owningEntity eq viewDef.viewDefinitionId }.limit(ApplicationData.maxRowsLimit)
            query.forEach {
                list.add(
                    AssociationDefinition(
                        it[table.id].value,
                        it[table.owningEntity],
                        it[table.ownedEntity],
                        it[table.name],
                        it[table.junctionEntityName],
                        it[table.owningType],
                        it[table.ownedType],
                        it[table.config] ?: ""
                    )
                )
            }
        }
        for (associationDefinition in list) {
            if (associationDefinition.owningEntity > 0) {
                associationDefinition.ownerViewDef = this.loadViewDef(associationDefinition.owningEntity)
            }
            if (associationDefinition.ownedEntity > 0) {
                associationDefinition.ownedViewDef = this.loadViewDef(associationDefinition.ownedEntity)
            }
        }
        return list;
    }

    private fun loadViewDef(entityId: Long): ViewDef? {
        val view: ViewDefinition = ViewDefinitionMapper.getById(entityId);
        val fields = FieldDefinitionMapper.getAll(mapOf("viewDefinitionId" to view.id))
        val childViews = ApplicationData.loadChildViews(view.id)
        val viewDef = ApplicationData.mapViewDefinitionToViewDef(view, fields, childViews)
        return viewDef
    }

    fun getAllAsOwned(viewDef: ViewDef): List<AssociationDefinition> {
        val list: MutableList<AssociationDefinition> = mutableListOf()
        transaction {
            addLogger(StdOutSqlLogger)
            val query: Query =
                table.select { table.ownedEntity eq viewDef.viewDefinitionId }.limit(ApplicationData.maxRowsLimit)
            query.forEach {
                list.add(
                    AssociationDefinition(
                        it[table.id].value,
                        it[table.owningEntity],
                        it[table.ownedEntity],
                        it[table.name],
                        it[table.junctionEntityName],
                        it[table.owningType],
                        it[table.ownedType],
                        it[table.config] ?: ""
                    )
                )
            }
        }
        for (associationDefinition in list) {
            if (associationDefinition.owningEntity > 0) {
                associationDefinition.ownerViewDef = this.loadViewDef(associationDefinition.owningEntity)
            }
            if (associationDefinition.ownedEntity > 0) {
                associationDefinition.ownedViewDef = this.loadViewDef(associationDefinition.ownedEntity)
            }
        }
        return list;
    }

}