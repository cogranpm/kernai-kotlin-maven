package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates

class Ingredient(
    override var id: Long = 0,
    name: String,
    quantity: Double,
    unit: String,
    var recipeId: Long
    ) : ModelObject(), IBeanDataEntity {


        var name: String by Delegates.observable(name, observer)
        var quantity: Double by Delegates.observable(quantity, observer)
        var unit: String by Delegates.observable(unit, observer)

        override fun getColumnValueByIndex(index: Int): String {
            return when (index) {
                0 -> name
                1 -> "$quantity"
                2 -> {
                    val listItem = LookupUtils.getLookupByKey(LookupUtils.unitLookupKey, false).find { it.code == unit}
                    "${listItem?.label}"
                }
                else -> ""
            }
        }
        override fun toString(): String {
            return "Ingredient(id=$id, name=$name, quantity=$quantity, unit=$unit, recipeId=$recipeId)"
        }

        companion object Factory {
            fun make(recipeId: Long): Ingredient {
                return Ingredient(0, "", 1.0, LookupUtils.getLookupByKey(LookupUtils.unitLookupKey, false)[0].code, recipeId)
            }
        }
}