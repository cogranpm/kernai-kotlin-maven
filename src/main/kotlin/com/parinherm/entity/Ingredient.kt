package com.parinherm.entity

import com.parinherm.ApplicationData
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
                2 -> unit
                else -> ""
            }
        }
        override fun toString(): String {
            return "Ingredient(id=$id, name=$name, quantity=$quantity, unit=$unit, recipeId=$recipeId)"
        }

        companion object Factory {
            fun make(recipeId: Long): Ingredient {
                return Ingredient(0, "", 0.0, ApplicationData.unitList[0].code, recipeId)
            }
        }
}