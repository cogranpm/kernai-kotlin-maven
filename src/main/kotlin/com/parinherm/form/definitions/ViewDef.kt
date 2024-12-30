package com.parinherm.form.definitions


import com.parinherm.entity.AssociationDefinition
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.AssociationDefinitionMapper
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class ViewDef(
    val viewDefinitionId: Long,
    val id: String,
    val title: String,
    val listWeight: Int = 1,
    val editWeight: Int = 3,
    val sashOrientation: SashOrientationDef = SashOrientationDef.VERTICAL,
    val fieldDefinitions: List<FieldDef>,
    val config: String,
    val entityDef: EntityDef,
    val childViews: List<ViewDef> = listOf(),
    val selfReference: Boolean = false, // does this allow a list of itself as a child, ie with a nullable parent id
    var referenceViewsList: MutableList<ViewDefinition>
) {

    // record the parents useful for stuff like code generation
    // store the id only, so we don't get a circular reference
   var parentViews: MutableSet<String> = mutableSetOf()

    val hasChildren: Boolean
    get () {
        return childViews.isNotEmpty()
    }

    val isChild: Boolean
    get() {
        return parentViews.isNotEmpty()
    }

    val configMap: Map<String, JsonElement>
        get() {
            val json = Json.parseToJsonElement(this.config)
            val map = json.jsonObject.toMap()
            return map
        }

    val ownerAssociations: List<AssociationDefinition>
        get() {
            return AssociationDefinitionMapper.getAllAsOwner(this);
        }

    val ownedAssociations: List<AssociationDefinition>
        get(){
            return AssociationDefinitionMapper.getAllAsOwned(this);
        }

    val searchFields : List<FieldDef>
        get() {
            return this.fieldDefinitions.filter { it.filterable }
        }

    val sortedFields : List<FieldDef>
        get(){
            return this.fieldDefinitions.sortedBy { it.sequence }
        }

     val showInListSortedFields : List<FieldDef>
        get(){
            return this.fieldDefinitions.filter { it.configMap.getOrDefault("showInList", "true") == "true" }.sortedBy { it.sequence }
        }


    init {
        childViews.forEach{
            it.parentViews.add(this.id)
        }


    }
}