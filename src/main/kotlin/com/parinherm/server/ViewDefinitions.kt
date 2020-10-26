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
                makeRecipesMap(),
                makeIngredientsMap(),
                makeTechSnippetsMap(),
                makeLoginsMap(),
                makeNotebooksMap(),
                makeNoteHeaderMap(),
                makeNoteDetailMap()
            )
        )
    }

    fun makeNotebooksMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val commentsDef = mapOf(
            ViewDef.fieldName to "comments",
            ViewDef.title to "Comments",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val viewDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.notebookViewId,
            ViewDef.title to "Notebooks",
            ViewDef.fields to listOf(nameDef, commentsDef),
            ViewDef.childViews to listOf(makeNoteHeaderMap())
        )

        return viewDef
    }

    fun makeNoteHeaderMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val commentsDef = mapOf(
            ViewDef.fieldName to "comments",
            ViewDef.title to "Comments",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val viewDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.noteheaderViewId,
            ViewDef.title to "Note Header",
            ViewDef.fields to listOf(nameDef, commentsDef),
            ViewDef.childViews to listOf(makeNoteDetailMap())
        )

        return viewDef
    }

    fun makeNoteDetailMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val bodyDef = mapOf(
            ViewDef.fieldName to "body",
            ViewDef.title to "Body",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val sourceCodeDef = mapOf(
            ViewDef.fieldName to "sourceCode",
            ViewDef.title to "Source",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val commentsDef = mapOf(
            ViewDef.fieldName to "comments",
            ViewDef.title to "Comments",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val viewDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.noteDetailViewId,
            ViewDef.title to "Note Detail",
            ViewDef.fields to listOf(nameDef, bodyDef, sourceCodeDef, commentsDef)
        )

        return viewDef
    }

    fun makeLoginsMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val categoryDef = mapOf(
            ViewDef.fieldName to "category",
            ViewDef.title to "Category",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.loginCategoryKey
        )

        val userNameDef = mapOf(
            ViewDef.fieldName to "userName",
            ViewDef.title to "User Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val passwordDef = mapOf(
            ViewDef.fieldName to "password",
            ViewDef.title to "Password",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.passwordMasterKey
        )

        val urlDef = mapOf(
            ViewDef.fieldName to "url",
            ViewDef.title to "URL",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.text
        )

        val notesDef = mapOf(
            ViewDef.fieldName to "notes",
            ViewDef.title to "Notes",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val otherDef = mapOf(
            ViewDef.fieldName to "other",
            ViewDef.title to "Other",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val viewDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.loginViewId,
            ViewDef.title to "Logins",
            ViewDef.fields to listOf(nameDef, categoryDef, userNameDef, passwordDef, urlDef, notesDef, otherDef)
        )

        return viewDef
    }


    fun makeTechSnippetsMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val langDef = mapOf(
            ViewDef.fieldName to "language",
            ViewDef.title to "Language",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.techLanguageLookupKey
        )


        val categoryDef = mapOf(
            ViewDef.fieldName to "category",
            ViewDef.title to "Category",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.snippetCategoryKey
        )


        val topicDef = mapOf(
            ViewDef.fieldName to "topic",
            ViewDef.title to "Topic",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.snippetTopicKey
        )

        val typeDef = mapOf(
            ViewDef.fieldName to "type",
            ViewDef.title to "Type",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.snippetTypeKey
        )

        val descDef = mapOf(
            ViewDef.fieldName to "desc",
            ViewDef.title to "Description",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.memo
        )

        val bodyDef = mapOf(
            ViewDef.fieldName to "body",
            ViewDef.title to "Body",
            ViewDef.required to false,
            ViewDef.fieldDataType to ViewDef.source
        )

        val outputDef = mapOf(
                ViewDef.fieldName to "output",
                ViewDef.title to "Output",
                ViewDef.required to false,
                ViewDef.fieldDataType to ViewDef.memo
        )


        val viewDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.techSnippetsViewId,
            ViewDef.title to "Snippets",
            ViewDef.sashWeights to intArrayOf(1, 3),
            ViewDef.sashOrientation to ViewDef.vertical,
            ViewDef.fields to listOf(nameDef, langDef, categoryDef, topicDef, typeDef, descDef, bodyDef, outputDef)
        )

        return viewDef

    }

    fun makeIngredientsMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val quantityDef = mapOf(
            ViewDef.fieldName to "quantity",
            ViewDef.title to "Quantity",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.float
        )

        val unitDef = mapOf(
            ViewDef.fieldName to "unit",
            ViewDef.title to "Unit",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.unitLookupKey
        )

        val ingredientsDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.ingredientViewId,
            ViewDef.title to "Ingredients",
            ViewDef.fields to listOf(nameDef, quantityDef, unitDef)
        )

        return ingredientsDef
    }

    fun makeRecipesMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDef.fieldName to "name",
            ViewDef.title to "Name",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.text
        )

        val categoryDef = mapOf(
            ViewDef.fieldName to "category",
            ViewDef.title to "Category",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.lookup,
            ViewDef.lookupKey to ApplicationData.recipeCategoryLookupKey
        )

        val methodDef = mapOf(
            ViewDef.fieldName to "method",
            ViewDef.title to "Method",
            ViewDef.required to true,
            ViewDef.fieldDataType to ViewDef.memo
        )


        val recipesDef = mapOf(
            ViewDef.viewid to ApplicationData.ViewDef.recipeViewId,
            ViewDef.title to "Recipes",
            ViewDef.fields to listOf(nameDef, methodDef, categoryDef),
            ViewDef.childViews to listOf(makeIngredientsMap())
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
            ViewDef.title to "People",
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