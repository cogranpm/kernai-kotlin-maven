package com.parinherm.entity

import com.parinherm.form.widgets.LookupPicker
import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates
import com.parinherm.form.widgets.ViewPicker

class FieldDefinition(
    override var id: Long = 0,
    var viewDefinitionId: Long,
    name: String, title: String,
    required: Boolean,
    size: String,
    dataType: String,
    lookupKey: String,
    filterable: Boolean,
    default: String,
    config: String,
    sequence: Int,
    length: Int?,
    referenceViewId: Long?
    /*,
    if the datatype is reference, which view definition are we referencing
   for example, if Lookup is the view selected, we show the Lookup Picker in the ui
   if View Definition is selected, we show the View Def picker
   so this field should only be enabled if DataType = Reference
   use case is for menus, which will have a View Picker field
   another use case if for this class FieldDefinition which has a lookup key field
  referenceViewId: String
    */
) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var title: String by Delegates.observable(title, observer)
    var required: Boolean by Delegates.observable(required, observer)
    var size: String by Delegates.observable(size, observer)
    var dataType: String by Delegates.observable(dataType, observer)
    var lookupKey: String by Delegates.observable(lookupKey, observer)
    var filterable: Boolean by Delegates.observable(filterable, observer)
    var default: String by Delegates.observable(default, observer)
    var config: String by Delegates.observable(config, observer)
    var sequence: Int by Delegates.observable(sequence, observer)
    var length: Int? by Delegates.observable(length, observer)
    var referenceViewId: Long? by Delegates.observable(referenceViewId, observer)


    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> title
            2 -> "$required"
            3 -> {
                val listItem =
                    LookupUtils.getLookupByKey(LookupUtils.fieldSizeLookupKey, false).find { it.code == size }
                "${listItem?.label}"
            }

            4 -> {
                val listItem =
                    LookupUtils.getLookupByKey(LookupUtils.dataTypeLookupKey, false).find { it.code == dataType }
                "${listItem?.label}"
            }

            5 -> {
                if (lookupKey != "") {
                    val listItem = LookupPicker.dataSource.find { it.key == lookupKey }
                    "${listItem?.label}"
                } else ""
            }

            6 -> "$filterable"
            7 -> "$default"
            8 -> "$sequence ?: 0"
            9 -> "$length"
            8 -> {
                if (referenceViewId != null) {
                    val listItem = ViewPicker.dataSource.find { it.id == referenceViewId}
                    "${listItem?.viewId}"
                } else ""
            }
            else -> ""
        }
    }

    override fun toString(): String {
        return "fieldDefinition(id=$id, name=$name, title=$title, required=$required, size=$size, dataType=$dataType, lookupKey=$lookupKey, filterable=$filterable, default=$default,)" // viewPickerId=$viewPickerId)"
    }

    companion object Factory {
        fun make(viewDefinitionId: Long): FieldDefinition {
            return FieldDefinition(
                0,
                viewDefinitionId,
                "",
                "",
                false,
                LookupUtils.getLookupByKey(LookupUtils.fieldSizeLookupKey, false)[0].code,
                LookupUtils.getLookupByKey(LookupUtils.dataTypeLookupKey, false)[0].code,
                LookupPicker.dataSource[0].key,
                false,
                "",
                "",
                0,
                0,
                0
            )
        }
    }
}