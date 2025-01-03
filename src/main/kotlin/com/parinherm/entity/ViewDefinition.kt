package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.properties.Delegates
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


class ViewDefinition(
    override var id: Long = 0,
    parentId: Long = 0,
    viewId: String,
    title: String,
    listWeight: Int,
    editWeight: Int,
    sashOrientation: String,
    entityName: String,
    config: String
) : ModelObject(), IBeanDataEntity {

    var viewId: String by Delegates.observable(viewId, observer)
    var title: String by Delegates.observable(title, observer)
    var listWeight: Int by Delegates.observable(listWeight, observer)
    var editWeight: Int by Delegates.observable(editWeight, observer)
    var sashOrientation: String by Delegates.observable(sashOrientation, observer)
    var entityName: String by Delegates.observable(entityName, observer)
    var parentId: Long by Delegates.observable(parentId, observer)
    var config: String by Delegates.observable(config, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> viewId
            1 -> title
            2 -> "$listWeight"
            3 -> "$editWeight"
            4 -> {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.sashOrientationLookupKey, false)
                    .find { it.code == sashOrientation }
                "${listItem?.label}"
            }

            5 -> entityName
            6 -> config

            else -> ""
        }
    }

    override fun toString(): String {
        return "viewDefinition(id=$id, viewId=$viewId, title=$title, listWeight=$listWeight, editWeight=$editWeight, sashOrientation=$sashOrientation, entityName=$entityName)"
    }


        val configMap: Map<String, String>
        get() {
            if(this.config.isNotEmpty()){
                val json = Json.parseToJsonElement(this.config)
                val map = json.jsonObject.toMutableMap().mapValues { it.value.jsonPrimitive.toString() }
                return map
            } else {
                return mapOf()
            }
        }

        val customTableName: String
        get() {
            val json = Json.parseToJsonElement(this.config)
            val map = json.jsonObject.toMap()
            //return map.getOrDefault("customTableName", "").toString()
            if(map.containsKey("customTableName")){
                return map.get("customTableName").toString()
            } else {
                return this.entityName
            }
        }


    companion object Factory {
        fun make(parentId: Long = 0): ViewDefinition {
            return ViewDefinition(
                0,
                parentId,
                "",
                "",
                0,
                0,
                LookupUtils.getLookupByKey(LookupUtils.sashOrientationLookupKey, false)[0].code,
                "",
                ""
            )
        }
    }
}