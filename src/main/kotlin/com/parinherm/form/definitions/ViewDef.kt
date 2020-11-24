package com.parinherm.form.definitions


import kotlinx.serialization.*

@Serializable
data class ViewDef(
    val id: String,
    val title: String,
    val listWeight: Int = 1,
    val editWeight: Int = 3,
    val sashOrientation: SashOrientationDef = SashOrientationDef.VERTICAL,
    val fieldDefinitions: List<FieldDef>,
    val childViews: List<ViewDef>
) {

    // record the parents useful for stuff like code generation
    // store the id only, so we don't get a circular reference
   var parentViews: MutableSet<String> = mutableSetOf()

    init {
        childViews.forEach{
            parentViews.add(this.id)
        }
    }
}