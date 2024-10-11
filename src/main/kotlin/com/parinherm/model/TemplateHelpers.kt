package com.parinherm.model

import com.parinherm.ApplicationData
import com.parinherm.entity.AssociationDefinition
import com.parinherm.entity.schema.AssociationDefinitionMapper
import com.parinherm.form.definitions.DataTypeDef
import com.parinherm.form.definitions.FieldDef
import com.parinherm.form.definitions.ViewDef
import com.parinherm.server.DefaultViewDefinitions
import java.util.*

class TemplateHelpers {

    fun getForeignKeysByView(viewDef: ViewDef): List<ViewDef> {
        return viewDef.parentViews.map {
            DefaultViewDefinitions.loadView(it, false)
        }
    }

    fun getOwnerAssociations(viewDef: ViewDef) : List<AssociationDefinition> {
        return AssociationDefinitionMapper.getAllAsOwner(viewDef);
    }

    fun getOwnedAssociations(viewDef: ViewDef) : List<AssociationDefinition> {
        return AssociationDefinitionMapper.getAllAsOwned(viewDef);
    }


    fun hasForeignKeys(viewDef: ViewDef): Boolean {
        return viewDef.parentViews.size > 0
    }

    fun capitalizeIt(name: String) : String {
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun lowercase(name: String): String {
        return name.lowercase()
    }
}