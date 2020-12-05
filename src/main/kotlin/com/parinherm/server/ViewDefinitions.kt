package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDefConstants
import com.parinherm.ApplicationData.publicationTypeLookupKey
import com.parinherm.form.definitions.*

object ViewDefinitions {
    fun makeViews(): List<ViewDef> = listOf(
            makeLogins(),
            makeSnippets(),
    ) + makeRecipes() + makeNotebooks() + makePerson()  + makeShelf() + makeLookup() + makeNoteSegmentTypeDef()

    private fun makeTextField(name: String, title: String, required: Boolean, filterable: Boolean = false): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.TEXT, null, filterable)
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
        lookupKey: String?,
        filterable: Boolean = false
    ): FieldDef {
        return FieldDef(name, title, required, size, dataType, lookupKey, filterable)
    }


    private fun makeNoteSegmentTypeDef(): List<ViewDef> {
        val noteSegmentTypeDef = ViewDef(ViewDefConstants.noteSegmentTypeViewId,
        "Note Segment Type", 2, 1, SashOrientationDef.VERTICAL,
        listOf(
            makeTextField("title", "Title", false),
            makeTextField("fontDesc", "Font", false),
            makeDateTimeField("createdDate", "Created", true)
        ),
            EntityDef("NoteSegmentType")
        )
        val noteSegmentTypeHeaderDef = ViewDef(ViewDefConstants.noteSegmentTypeHeaderViewId,
        "Note Segment Type Header", 2, 1, SashOrientationDef.VERTICAL,
        listOf(
            makeTextField("title", "Title", true),
            makeMemoField("helpText", "Help", false),
            makeDateTimeField("createdDate", "Created", true)
        ),
            EntityDef("NoteSegmentTypeHeader"),
            listOf(noteSegmentTypeDef)
        )

        return listOf(noteSegmentTypeHeaderDef, noteSegmentTypeDef)
    }

    private fun makeShelf(): List<ViewDef> {
        val name = makeTextField("title", "Title", true)
        val comments = makeMemoField("comments", "Comments", false)
        val createdDate = makeDateTimeField("createdDate", "Created", false)

        val noteDef = ViewDef(ViewDefConstants.noteViewId, "Note", 3, 1, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", true),
                makeMemoField("description", "Description", false),
                makeTextField("titleAudioFile", "Title Audio", false),
                makeTextField("descriptionAudioFile", "Description Audio", false),
                makeDateTimeField("createdDate", "Created", true)
            ),
            EntityDef("Note"),
            listOf())


        val topicDef = ViewDef(ViewDefConstants.topicViewId, "Topic", 3, 1, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("name", "Name", true),
                makeMemoField("comments", "Comments", false),
                makeDateTimeField("createdDate", "Created", true)
            ),
            EntityDef("Topic"),
            listOf(noteDef))

        val publicationDef = ViewDef(ViewDefConstants.publicationViewId, "Publication", 3, 1, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", true),
                makeLookupField("type", "Type", false, publicationTypeLookupKey),
                makeMemoField("comments", "Comments", false),
                makeDateTimeField("createdDate", "Created", true)
            ),
            EntityDef("Publication"),
            listOf(topicDef)
        )

        val subjectDef = ViewDef(ViewDefConstants.subjectViewId, "Subject", 2, 1, SashOrientationDef.VERTICAL,
        listOf(
            makeTextField("title", "Title", true),
            makeMemoField("comments", "Comments", false),
            makeDateTimeField("createdDate", "Created", true)
        ),
            EntityDef("Subject"),
            listOf(publicationDef))

        val view = ViewDef(ViewDefConstants.shelfViewId, "Shelf", 2, 1, SashOrientationDef.VERTICAL,
            listOf(name, comments, createdDate),
            EntityDef("Shelf"),
            listOf(subjectDef))
        return listOf(view, subjectDef, publicationDef, topicDef, noteDef) + makeNoteSegmentTypeDef()
    }

    private fun makeLookupDetail() : ViewDef {
        val code = makeTextField("code", "Code", true)
        val label = makeTextField("label", "Label", true)
        return ViewDef(ApplicationData.ViewDefConstants.lookupDetailViewId, "Lookup Items", 1, 3, SashOrientationDef.VERTICAL,
        listOf(code, label), EntityDef("lookupdetail"), emptyList())
    }

    private fun makeLookup(): List<ViewDef> {
        val key = makeTextField("key", "Key", true)
        val label = makeTextField("label", "Label", true)
        val lookupDetailDef = makeLookupDetail()
        val view = ViewDef(ApplicationData.ViewDefConstants.lookupViewId,
        "Lookups", 1, 3, SashOrientationDef.VERTICAL, listOf(key, label), EntityDef("lookup"), listOf(lookupDetailDef))
        return listOf(view, lookupDetailDef)
    }

    private fun makePerson(): List<ViewDef> {
        val name = makeTextField("name", "First Name", true)
        val income = makeMoneyField("income", "Income", true)
        val height = makeFloatField("height", "Height", true)
        val age = makeIntField("age", "Age", true)
        val country = makeLookupField("country", "Country", true, ApplicationData.countryLookupKey)
        val enteredDate = makeDateTimeField("enteredDate", "Entered", true)
        val isDeceased = makeBooleanField("deceased", "Deceased", true)
        val personDetailDef = makePersonDetail()
        val view = ViewDef(ApplicationData.ViewDefConstants.personViewId, "People", 1, 3, SashOrientationDef.VERTICAL,
            listOf(name, income, height, age, country, enteredDate, isDeceased),
            EntityDef("person"),
            listOf(personDetailDef)
        )
        return listOf(view, personDetailDef)
    }

    private fun makePersonDetail(): ViewDef {
        val nickname = makeTextField("nickname", "Nickname", true)
        val petSpecies = makeLookupField("petSpecies", "Pet", true, ApplicationData.speciesLookupKey)
        return ViewDef(
            ApplicationData.ViewDefConstants.personDetailsViewId, "Person Details", 1, 3, SashOrientationDef.VERTICAL,
            listOf(nickname, petSpecies), EntityDef("persondetail"), listOf()
        )
    }

    private fun makeIngredients(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val quantity = makeFloatField("quantity", "Quantity", true)
        val unit = makeLookupField("unit", "Unit", true, ApplicationData.unitLookupKey)
        return ViewDef(
            ApplicationData.ViewDefConstants.ingredientViewId,
            "Ingredients", 1, 3, SashOrientationDef.VERTICAL,
            listOf(name, quantity, unit),
            EntityDef("ingredient"),
            listOf()
        )
    }

    private fun makeRecipes(): List<ViewDef> {
        val name = makeTextField("name", "Name", true, filterable = true)
        val category = makeLookupField("category", "Category", true, ApplicationData.recipeCategoryLookupKey)
        val method = makeMemoField("method", "Method", true)
        val ingredients = makeIngredients()
        return listOf(ViewDef(
            ViewDefConstants.recipeViewId, "Recipe", 1, 3, SashOrientationDef.VERTICAL,
            listOf(name, category, method),
            EntityDef("recipe"),
            listOf(ingredients)
        ) ,ingredients)

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
            EntityDef("snippet"),
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
            EntityDef("login"),
            listOf()
        )
        return view
    }


    private fun makeNotebooks(): List<ViewDef> {
        val name = makeTextField("name", "Name", true, filterable = true)
        val comments = makeMemoField("comments", "Comments", false)
        val noteDetailDef = makeNoteDetails()
        val noteHeaderDef = makeNoteHeaders(noteDetailDef)
        val view = ViewDef(
            ViewDefConstants.notebookViewId, "Notebooks", 3, 1, SashOrientationDef.VERTICAL, listOf(name, comments),
            EntityDef("notebook"),
            listOf(noteHeaderDef)
        )
        return listOf(view, noteDetailDef, noteHeaderDef)
    }

    private fun makeNoteHeaders(noteDetailDef: ViewDef): ViewDef {
        val name = makeTextField("name", "Name", true, filterable = true)
        val comments = makeMemoField("comments", "Comments", false)
        val view = ViewDef(
            ViewDefConstants.noteheaderViewId,
            "Note Header",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, comments),
            EntityDef("noteheader"),
            listOf(
               noteDetailDef
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
            ViewDefConstants.noteDetailViewId,
            "Note Detail",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, body, sourceCode, comments),
            EntityDef("notedetail"),
            listOf()
        )
        return view
    }

}