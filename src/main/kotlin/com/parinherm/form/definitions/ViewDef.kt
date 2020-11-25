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
    val entityDef: EntityDef,
    val childViews: List<ViewDef> = listOf(),
    val references: List<ViewDef> = listOf() // composition or 1 to 1 relationship
) {

    // record the parents useful for stuff like code generation
    // store the id only, so we don't get a circular reference
    // this won't work, its not a unified instance of child view def
   var parentViews: MutableSet<String> = mutableSetOf()

    init {
        childViews.forEach{
            it.parentViews.add(this.id)
        }
    }
}