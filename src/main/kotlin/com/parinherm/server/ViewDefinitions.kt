package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDef

object ViewDefinitions {

    fun makeDefinitions(): Map<String, Any> {
        return mapOf(
            ViewDef.version to 1,
            ViewDef.forms to listOf(
                makePersonMap(),
                makePersonDetailDef(),
                makeRecipesMap()
            )
        )
    }

    fun makeRecipesMap(): Map<String, Any> {
        /****************** child bean person details ****************/
        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val methodDef = mapOf(
            ViewDef.fieldName to "method",
            ViewDef.title to "Method",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.memo
        )
        val categoryDef = mapOf(
            ViewDef.fieldName to "category",
            ViewDef.title to "Category",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.recipeCategoryLookupKey
        )

        val recipesDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.recipeViewId,
            ViewDef.title to "Recipes",
            ViewDef.fields to listOf(nameDef, methodDef, categoryDef)
        )
        return recipesDef
    }

    fun makePersonDetailDef(): Map<String, Any> {
        /****************** child bean person details ****************/
        val nicknameDef = mapOf(
            ViewDef.fieldName to "nickname",
            ViewDef.title to "Nickname",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val petSpeciesDef = mapOf(
            ViewDef.fieldName to "petSpecies",
            ViewDef.title to "Pet",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.speciesLookupKey
        )

        val personDetailsDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.personDetailsViewId,
            ViewDef.title to "Persons Details",
            ViewDef.fields to listOf(nicknameDef, petSpeciesDef)
        )

        return personDetailsDef
    }

    fun makePersonMap(): Map<String, Any> {
        val firstNameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "First Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val incomeDef = mapOf(
            ViewDef.fieldName to "income",
            ViewDef.title to "Income",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.money
        )

        val heightDef = mapOf(
            ViewDef.fieldName to "height",
            ViewDef.title to "Height",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.float
        )

        val ageDef = mapOf(
            ViewDef.fieldName to "age",
            ViewDef.title to "Age",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.int
        )

        val countryDef = mapOf(
            ViewDef.fieldName to "country",
            ViewDef.title to "Country",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.countryLookupKey
        )

        val enteredDateDef = mapOf(
            ViewDef.fieldName to "enteredDate",
            ViewDef.title to "Entered",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.datetime
        )


        val isDeceasedDef = mapOf(
            ViewDef.fieldName to "deceased",
            ViewDef.title to "Deceased",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.bool
        )


        val viewDef = mapOf(
            ViewDef.viewid to ViewDef.personViewId,
            ViewDef.title to "Beans Binding Test",
            ViewDef.fields to listOf(
                firstNameDef,
                incomeDef,
                heightDef,
                ageDef,
                countryDef,
                enteredDateDef,
                isDeceasedDef
            ),
            ViewDef.childViews to listOf(makePersonDetailDef())
        )
        return viewDef

    }
}