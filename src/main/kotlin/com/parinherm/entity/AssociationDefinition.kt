package com.parinherm.entity

import com.parinherm.form.definitions.ViewDef
import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates
import com.parinherm.entity.schema.FieldDefinitionMapper
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.ApplicationData

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
    private var _ownedViewDef: ViewDef? = null
    private var _ownerViewDef: ViewDef? = null


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

    var ownedViewDef: ViewDef?
        get() = this._ownedViewDef
        set(value) {
          this._ownedViewDef = value
        }

    var ownerViewDef: ViewDef?
        get() = this._ownerViewDef
        set(value) {
          this._ownerViewDef = value
        }

    /*
    fun ownedViewDef(): ViewDef?{
        if(this._ownedViewDef == null){
            this._ownedViewDef = this.loadViewDef(ownedEntity)
        }
        return this._ownedViewDef
    }

    fun ownerViewDef() : ViewDef?{
        if(this._ownerViewDef == null){
            this._ownerViewDef = this.loadViewDef(owningEntity)
        }
        return this._ownerViewDef
    }

     */


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