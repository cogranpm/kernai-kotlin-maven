package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDefConstants
import com.parinherm.form.definitions.*

object ViewDefinitions {
    fun makeViews(): List<ViewDef> = listOf(
        makePerson(),
        makePersonDetail(),
        makeRecipes(),
        makeIngredients(),
        makeLogins(),
        makeNotebooks(),
        makeNoteHeaders(),
        makeNoteDetails(),
        makeSnippets()
    )

    private fun makeTextField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.TEXT, null)
    }

    private fun makeMemoField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.LARGE, DataTypeDef.MEMO, null)
    }

    private fun makeSourceField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.LARGE, DataTypeDef.SOURCE, null)
    }

    private fun makeLookupField(name: String, title: String, required: Boolean, lookupKey: String): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.LOOKUP, lookupKey)
    }

    private fun makeIntField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.INT, null)
    }

    private fun makeFloatField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.FLOAT, null)
    }

    private fun makeDateTimeField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.DATETIME, null)
    }

    private fun makeMoneyField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.MONEY, null)
    }

    private fun makeBooleanField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.SMALL, DataTypeDef.BOOLEAN, null)
    }

    private fun makeField(
        name: String,
        title: String,
        required: Boolean,
        size: SizeDef,
        dataType: DataTypeDef,
        lookupKey: String?
    ): FieldDef {
        return FieldDef(name, title, required, size, dataType, lookupKey)
    }

    private fun makePerson(): ViewDef {
        val name = makeTextField("name", "First Name", true)
        val income = makeMoneyField("income", "Income", true)
        val height = makeFloatField("height", "Height", true)
        val age = makeIntField("age", "Age", true)
        val country = makeLookupField("country", "Country", true, ApplicationData.countryLookupKey)
        val enteredDate = makeDateTimeField("enteredDate", "Entered", true)
        val isDeceased = makeBooleanField("deceased", "Deceased", true)
        return ViewDef(ApplicationData.ViewDefConstants.personViewId, "People", 1, 3, SashOrientationDef.VERTICAL,
            listOf(name, income, height, age, country, enteredDate, isDeceased), listOf(makePersonDetail()))
    }

    private fun makePersonDetail(): ViewDef {
        val nickname = makeTextField("nickname", "Nickname", true)
        val petSpecies = makeLookupField("petSpecies", "Pet", true, ApplicationData.speciesLookupKey)
        return ViewDef(
            ApplicationData.ViewDefConstants.personDetailsViewId, "Person Details", 1, 3, SashOrientationDef.VERTICAL,
            listOf(nickname, petSpecies), listOf()
        )
    }

    private fun makeIngredients(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val quantity = makeFloatField("quantity", "Quantity", true)
        val unit = makeLookupField("unit", "Unit", true, ApplicationData.unitLookupKey)
        return ViewDef(
            ApplicationData.ViewDefConstants.ingredientViewId,
            "Ingredients", 1, 3, SashOrientationDef.VERTICAL, listOf(name, quantity, unit), listOf()
        )
    }

    private fun makeRecipes(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val category = makeLookupField("category", "Category", true, ApplicationData.recipeCategoryLookupKey)
        val method = makeMemoField("method", "Method", true)
        return ViewDef(
            ApplicationData.ViewDefConstants.recipeViewId, "Recipes", 1, 3, SashOrientationDef.VERTICAL,
            listOf(name, category, method), listOf(makeIngredients())
        )
    }

    private fun makeSnippets(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val language = makeLookupField("language", "Language", true, ApplicationData.techLanguageLookupKey)
        val category = makeLookupField("category", "Category", true, ApplicationData.snippetCategoryKey)
        val topic = makeLookupField("topic", "Topic", true, ApplicationData.snippetTopicKey)
        val type = makeLookupField("type", "Type", true, ApplicationData.snippetTypeKey)
        val desc = makeMemoField("desc", "Description", false)
        val body = makeSourceField("body", "Body", false)
        val output = makeMemoField("output", "Output", false)
        return ViewDef(
            ApplicationData.ViewDefConstants.techSnippetsViewId,
            "Snippets",
            1,
            5,
            SashOrientationDef.HORIZONTAL,
            listOf(name, language, category, topic, type, desc, body, output),
            listOf()
        )
    }


    private fun makeLogins(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val category = makeLookupField("category", "Category", true, ApplicationData.loginCategoryKey)
        val userName = makeTextField("userName", "User Name", true)
        val password = makeLookupField("password", "Password", true, ApplicationData.passwordMasterKey)
        val url = makeTextField("url", "URL", false)
        val notes = makeMemoField("notes", "Notes", false)
        val other = makeMemoField("other", "Other", false)
        val view = ViewDef(
            ViewDefConstants.loginViewId,
            "Login",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, category, userName, password, url, notes, other),
            listOf()
        )
        return view
    }


    private fun makeNotebooks(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val comments = makeMemoField("comments", "Comments", false)
        val view = ViewDef(
            ViewDefConstants.notebookViewId, "Notebooks", 1, 3, SashOrientationDef.VERTICAL, listOf(name, comments),
            listOf(makeNoteHeaders())
        )
        return view
    }

    private fun makeNoteHeaders(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val comments = makeMemoField("comments", "Comments", false)
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

    private fun makeNoteDetails(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val body = makeMemoField("body", "Body", false)
        val sourceCode = makeMemoField("sourceCode", "Source", false)
        val comments = makeMemoField("comments", "Comments", false)
        val view = ViewDef(
            ViewDefConstants.noteheaderViewId,
            "Note Header",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, body, sourceCode, comments),
            listOf()
        )
        return view
    }

}