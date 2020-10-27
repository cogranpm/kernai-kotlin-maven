package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDefConstants
import com.parinherm.form.definitions.*

object ViewDefinitions {

    fun makeDefinitions(): Map<String, Any> {
        return mapOf(
            ViewDefConstants.version to 1,
            ViewDefConstants.forms to listOf(
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

    fun makeNotebooks(): ViewDef {
        val name = FieldDef("name", "Name", true, SizeDef.SMALL, DataTypeDef.TEXT)
        val comments = FieldDef("comments", "Comments", false, SizeDef.MEDIUM, DataTypeDef.MEMO)
        val view = ViewDef(
            ViewDefConstants.notebookViewId, "Notebooks", 1, 3, SashOrientationDef.VERTICAL, listOf(name, comments),
            listOf(makeNoteHeaders())
        )
        return view
    }

    fun makeNoteHeaders(): ViewDef {
        val name = FieldDef("name", "Name", true, SizeDef.SMALL, DataTypeDef.TEXT)
        val comments = FieldDef("comments", "Comments", false, SizeDef.MEDIUM, DataTypeDef.MEMO)
        val view = ViewDef(
            ViewDefConstants.noteheaderViewId,
            "Note Header",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, comments),
            listOf(
                makeNoteDetails()
            )
        )
        return view
    }


    fun makeNotebooksMap(): Map<String, Any> {


        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val commentsDef = mapOf(
            ViewDefConstants.fieldName to "comments",
            ViewDefConstants.title to "Comments",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val viewDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.notebookViewId,
            ViewDefConstants.title to "Notebooks",
            ViewDefConstants.fields to listOf(nameDef, commentsDef),
            ViewDefConstants.childViews to listOf(makeNoteHeaderMap())
        )

        return viewDef

    }


    fun makeNoteHeaderMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val commentsDef = mapOf(
            ViewDefConstants.fieldName to "comments",
            ViewDefConstants.title to "Comments",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val viewDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.noteheaderViewId,
            ViewDefConstants.title to "Note Header",
            ViewDefConstants.fields to listOf(nameDef, commentsDef),
            ViewDefConstants.childViews to listOf(makeNoteDetailMap())
        )

        return viewDef
    }

    fun makeNoteDetails(): ViewDef {
        val name = FieldDef("name", "Name", true, SizeDef.SMALL, DataTypeDef.TEXT)
        val comments = FieldDef("comments", "Comments", false, SizeDef.MEDIUM, DataTypeDef.MEMO)
        val view = ViewDef(
            ViewDefConstants.noteheaderViewId,
            "Note Header",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, comments),
            listOf()
        )
        return view
    }

    fun makeNoteDetailMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val bodyDef = mapOf(
            ViewDefConstants.fieldName to "body",
            ViewDefConstants.title to "Body",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val sourceCodeDef = mapOf(
            ViewDefConstants.fieldName to "sourceCode",
            ViewDefConstants.title to "Source",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val commentsDef = mapOf(
            ViewDefConstants.fieldName to "comments",
            ViewDefConstants.title to "Comments",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val viewDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.noteDetailViewId,
            ViewDefConstants.title to "Note Detail",
            ViewDefConstants.fields to listOf(nameDef, bodyDef, sourceCodeDef, commentsDef)
        )

        return viewDef
    }

    fun makeLoginsMap(): Map<String, Any> {

        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val categoryDef = mapOf(
            ViewDefConstants.fieldName to "category",
            ViewDefConstants.title to "Category",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.loginCategoryKey
        )

        val userNameDef = mapOf(
            ViewDefConstants.fieldName to "userName",
            ViewDefConstants.title to "User Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val passwordDef = mapOf(
            ViewDefConstants.fieldName to "password",
            ViewDefConstants.title to "Password",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.passwordMasterKey
        )

        val urlDef = mapOf(
            ViewDefConstants.fieldName to "url",
            ViewDefConstants.title to "URL",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val notesDef = mapOf(
            ViewDefConstants.fieldName to "notes",
            ViewDefConstants.title to "Notes",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val otherDef = mapOf(
            ViewDefConstants.fieldName to "other",
            ViewDefConstants.title to "Other",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val viewDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.loginViewId,
            ViewDefConstants.title to "Logins",
            ViewDefConstants.fields to listOf(
                nameDef,
                categoryDef,
                userNameDef,
                passwordDef,
                urlDef,
                notesDef,
                otherDef
            )
        )

        return viewDef
    }


    fun makeTechSnippetsMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val langDef = mapOf(
            ViewDefConstants.fieldName to "language",
            ViewDefConstants.title to "Language",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.techLanguageLookupKey
        )


        val categoryDef = mapOf(
            ViewDefConstants.fieldName to "category",
            ViewDefConstants.title to "Category",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.snippetCategoryKey
        )


        val topicDef = mapOf(
            ViewDefConstants.fieldName to "topic",
            ViewDefConstants.title to "Topic",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.snippetTopicKey
        )

        val typeDef = mapOf(
            ViewDefConstants.fieldName to "type",
            ViewDefConstants.title to "Type",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.snippetTypeKey
        )

        val descDef = mapOf(
            ViewDefConstants.fieldName to "desc",
            ViewDefConstants.title to "Description",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )

        val bodyDef = mapOf(
            ViewDefConstants.fieldName to "body",
            ViewDefConstants.title to "Body",
            ViewDefConstants.required to false,
            ViewDefConstants.sizeHint to ViewDefConstants.large,
            ViewDefConstants.fieldDataType to ViewDefConstants.source
        )

        val outputDef = mapOf(
            ViewDefConstants.fieldName to "output",
            ViewDefConstants.title to "Output",
            ViewDefConstants.required to false,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )


        val viewDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.techSnippetsViewId,
            ViewDefConstants.title to "Snippets",
            ViewDefConstants.listWeight to 1,
            ViewDefConstants.editWeight to 5,
            ViewDefConstants.sashOrientation to ViewDefConstants.horizontal,
            ViewDefConstants.fields to listOf(
                nameDef,
                langDef,
                categoryDef,
                topicDef,
                typeDef,
                descDef,
                bodyDef,
                outputDef
            )
        )

        return viewDef

    }

    fun makeIngredientsMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val quantityDef = mapOf(
            ViewDefConstants.fieldName to "quantity",
            ViewDefConstants.title to "Quantity",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.float
        )

        val unitDef = mapOf(
            ViewDefConstants.fieldName to "unit",
            ViewDefConstants.title to "Unit",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.unitLookupKey
        )

        val ingredientsDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.ingredientViewId,
            ViewDefConstants.title to "Ingredients",
            ViewDefConstants.fields to listOf(nameDef, quantityDef, unitDef)
        )

        return ingredientsDef
    }

    fun makeRecipesMap(): Map<String, Any> {
        val nameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val categoryDef = mapOf(
            ViewDefConstants.fieldName to "category",
            ViewDefConstants.title to "Category",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.recipeCategoryLookupKey
        )

        val methodDef = mapOf(
            ViewDefConstants.fieldName to "method",
            ViewDefConstants.title to "Method",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.memo
        )


        val recipesDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.recipeViewId,
            ViewDefConstants.title to "Recipes",
            ViewDefConstants.fields to listOf(nameDef, methodDef, categoryDef),
            ViewDefConstants.childViews to listOf(makeIngredientsMap())
        )
        return recipesDef
    }

    fun makePersonDetailDef(): Map<String, Any> {
        /****************** child bean person details ****************/
        val nicknameDef = mapOf(
            ViewDefConstants.fieldName to "nickname",
            ViewDefConstants.title to "Nickname",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val petSpeciesDef = mapOf(
            ViewDefConstants.fieldName to "petSpecies",
            ViewDefConstants.title to "Pet",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.speciesLookupKey
        )

        val personDetailsDef = mapOf(
            ViewDefConstants.viewid to ApplicationData.ViewDefConstants.personDetailsViewId,
            ViewDefConstants.title to "Persons Details",
            ViewDefConstants.fields to listOf(nicknameDef, petSpeciesDef)
        )

        return personDetailsDef
    }

    fun makePersonMap(): Map<String, Any> {
        val firstNameDef = mapOf(
            ViewDefConstants.fieldName to "name",
            ViewDefConstants.title to "First Name",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.text
        )

        val incomeDef = mapOf(
            ViewDefConstants.fieldName to "income",
            ViewDefConstants.title to "Income",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.money
        )

        val heightDef = mapOf(
            ViewDefConstants.fieldName to "height",
            ViewDefConstants.title to "Height",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.float
        )

        val ageDef = mapOf(
            ViewDefConstants.fieldName to "age",
            ViewDefConstants.title to "Age",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.int
        )

        val countryDef = mapOf(
            ViewDefConstants.fieldName to "country",
            ViewDefConstants.title to "Country",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.lookup,
            ViewDefConstants.lookupKey to ApplicationData.countryLookupKey
        )

        val enteredDateDef = mapOf(
            ViewDefConstants.fieldName to "enteredDate",
            ViewDefConstants.title to "Entered",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.datetime
        )


        val isDeceasedDef = mapOf(
            ViewDefConstants.fieldName to "deceased",
            ViewDefConstants.title to "Deceased",
            ViewDefConstants.required to true,
            ViewDefConstants.fieldDataType to ViewDefConstants.bool
        )


        val viewDef = mapOf(
            ViewDefConstants.viewid to ViewDefConstants.personViewId,
            ViewDefConstants.title to "People",
            ViewDefConstants.fields to listOf(
                firstNameDef,
                incomeDef,
                heightDef,
                ageDef,
                countryDef,
                enteredDateDef,
                isDeceasedDef
            ),
            ViewDefConstants.childViews to listOf(makePersonDetailDef())
        )
        return viewDef

    }
}