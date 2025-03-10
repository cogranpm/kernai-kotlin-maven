package com.parinherm.form.definitions


import com.parinherm.entity.AssociationDefinition
import com.parinherm.entity.Lookup
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.AssociationDefinitionMapper
import com.parinherm.lookups.LookupUtils
import kotlinx.serialization.*
import kotlinx.serialization.json.*

//@Serializable
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
        get() {
            return childViews.isNotEmpty()
        }

    val isChild: Boolean
        get() {
            return parentViews.isNotEmpty()
        }

    val configMap: Map<String, JsonElement>
        get() {
            if(this.config.isNotEmpty()) {
                val json = Json.parseToJsonElement(this.config)
                val map = json.jsonObject.toMap()
                //return map.mapValues { it.value.toString().replace("\"", "") }
                return map
            } else {
                return emptyMap()
            }
        }

    val customTableName: String
        get() {
            if (this.config.isNotEmpty()) {
                val json = Json.parseToJsonElement(this.config)
                val map = json.jsonObject.toMap()
                //return map.getOrDefault("customTableName", "").toString()
                if (map.containsKey("customTableName")) {
                    return map.get("customTableName").toString()
                } else {
                    return this.entityDef.name
                }
            } else {
                return this.entityDef.name
            }

        }

    val ownerAssociations: List<AssociationDefinition>
        get() {
            return AssociationDefinitionMapper.getAllAsOwner(this);
        }

    val ownedAssociations: List<AssociationDefinition>
        get() {
            return AssociationDefinitionMapper.getAllAsOwned(this);
        }

    val searchFields: List<FieldDef>
        get() {
            return this.fieldDefinitions.filter { it.filterable }
        }

    val sortedFields: List<FieldDef>
        get() {
            return this.fieldDefinitions.sortedBy { it.sequence }
        }



    val showInListSortedFields: List<FieldDef>
        get() {
            return this.fieldDefinitions.filter { it.configMap.getOrDefault("showInList", "true") == "true" }
                .sortedBy { it.sequence }
        }

    val simpleLookups: List<Lookup>
        get() {
            return this.fieldDefinitions.filter {
                !it.lookupKey.isNullOrEmpty() &&
                        it.configMap.getOrDefault("advancedLookup", "false") == "false"
            }.distinctBy{it.lookupKey}.map { LookupUtils.getLookupByKey(it.lookupKey.toString()) }
        }

    val linqSortBy: String
        get() {
            if(this.configMap.containsKey("sortBy") ){
                val sortBy =  this.configMap["sortBy"];
                var fullSortBy = "";
                var index = 0;
                val sortByArray = sortBy as JsonArray
                for (item: JsonElement in sortByArray)
                {
                    var orderClause = if (index == 0)  "OrderBy" else ".ThenBy"
                    var itemObject = item as JsonObject
                    val name = itemObject["name"]
                    val dir = itemObject["dir"]
                    fullSortBy += orderClause
                    if(dir.toString() == "desc") {
                        fullSortBy += "Descending"
                    }
                    fullSortBy += "(x => x.$name)"
                    index++
                }
               return fullSortBy;
            } else {
                return "";
            }
        }


    init {
        childViews.forEach {
            it.parentViews.add(this.id)
        }


    }
}