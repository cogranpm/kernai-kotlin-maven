package com.parinherm.entity

import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates

class AssociationDefinition(
    override var id: Long = 0,
    owningEntity: Long = 0,
    ownedEntity: Long = 0,
    name: String,
    junctionEntityName: String,
    owningType: String,
    ownedType: String,
    config: String
): ModelObject(), IBeanDataEntity  {

    var name: String by Delegates.observable(name, observer)
    var owningEntity: Long by Delegates.observable(owningEntity, observer)
    var ownedEntity: Long by Delegates.observable(ownedEntity, observer)
    var junctionEntityName: String by Delegates.observable(junctionEntityName, observer)
    var owningType: String by Delegates.observable(owningType, observer)
    var ownedType: String by Delegates.observable(ownedType, observer)
    var config: String by Delegates.observable(config, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> ViewDefinitionMapper.getById(this.owningEntity)?.entityName ?: ""
            2 -> ViewDefinitionMapper.getById(this.ownedEntity)?.entityName ?: ""
            3 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.associationTypeLookupKey, false).find { it.code == owningType}
                "${listItem?.label}"
            }
            4 -> junctionEntityName
            5 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.associationTypeLookupKey, false).find { it.code == ownedType}
                "${listItem?.label}"
            }
            else -> ""
        }
    }

    override fun toString(): String {
        return "viewDefinition(id=$id, name=$name, owningEntity=$owningEntity, owwnedEntity=$ownedEntity, junctionEntityName=$junctionEntityName, owningType=$owningType, ownedType=$ownedType)"
    }

    fun ownedViewDefinition(): ViewDefinition {
       return ViewDefinitionMapper.getById(ownedEntity)
    }

    fun ownerViewDefinition() : ViewDefinition {
        return ViewDefinitionMapper.getById(owningEntity)
    }


    companion object Factory {
        fun make(): AssociationDefinition {
            return AssociationDefinition(
                0,
                0 ,
                0 ,
                "" ,
                "",
                LookupUtils.getLookupByKey(LookupUtils.associationTypeLookupKey, false)[0].code,
                LookupUtils.getLookupByKey(LookupUtils.associationTypeLookupKey, false)[0].code,
                ""
            )
        }
    }
}