package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.entity.FieldDefinition
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.FieldDefinitionMapper
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.form.definitions.*
import com.parinherm.lookups.LookupUtils

object DefaultViewDefinitions {

    private fun makeField(
        name: String,
        title: String,
        required: Boolean,
        size: SizeDef,
        dataType: DataTypeDef,
        lookupKey: String?,
        filterable: Boolean = false,
        default: String = "",
        config: String = "",
        sequence: Int = 0,
        referenceDef: ReferenceDef?
    ): FieldDefinition {
        /* mappings for the enums */
        return FieldDefinition(
            0,
            0,
            name,
            title,
            required,
            SizeDef.mappedSize(size),
            DataTypeDef.mappedDataType(dataType),
            lookupKey ?: "",
            filterable,
            default,
            config,
            sequence
        )
    }

    private fun makeTextField(
        name: String,
        title: String,
        required: Boolean,
        filterable: Boolean = false
    ): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.TEXT, null, filterable, "",  "", 0,null)
    }

    private fun makeMemoField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.LARGE, DataTypeDef.MEMO, "", false, "", "",0, null)
    }

    private fun makeSourceField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.LARGE, DataTypeDef.SOURCE, "", false, "", "",0, null)
    }

    private fun makeLookupField(name: String, title: String, required: Boolean, lookupKey: String): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.LOOKUP, lookupKey, false, "", "",0, null)
    }

    private fun makeIntField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.INT, "", false, "", "",0,null)
    }

    private fun makeFloatField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.FLOAT, "", false, "", "",0,null)
    }

    private fun makeDateTimeField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.DATETIME, "", false, "","",0, null)
    }

    private fun makeDateField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.DATE, "", false, "","",0, null)
    }

    private fun makeMoneyField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.MONEY, "", false, "","",0, null)
    }

    private fun makeBooleanField(name: String, title: String, required: Boolean): FieldDefinition {
        return makeField(name, title, required, SizeDef.SMALL, DataTypeDef.BOOLEAN, "", false, "","",0, null)
    }

    private fun makeReferenceField(
        name: String,
        title: String,
        required: Boolean,
        referenceDef: ReferenceDef
    ): FieldDefinition {
        return makeField(name, title, required, SizeDef.SMALL, DataTypeDef.REFERENCE, null, false, "","",0, referenceDef)
    }


    /* this one is just a front end to existing back end LookupDetail */
    private fun makePasswordForm() {

        val fields = listOf(
            makeTextField("code", "Password", true),
            makeTextField("label", "Nickname", true),
        )
        val passwordFormDef = ViewDefinition(0, 0,
            ViewDefConstants.passwordsViewId,
            "Passwords",
            2,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "LookupDetail",
            ""
        )
        ViewDefinitionMapper.createDefault(passwordFormDef, fields)

    }


    private fun makeNoteSegmentTypeDef() : ViewDefinition {

       val fields = listOf(
           makeTextField("title", "Title", true),
           makeMemoField("helpText", "Help", false),
           makeDateTimeField("createdDate", "Created", true)
       )
        val noteSegmentTypeHeaderDef = ViewDefinition(0, 0,
            ViewDefConstants.noteSegmentTypeHeaderViewId,
            "Note Segment Type Header",
            2,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "NoteSegmentTypeHeader",
            ""
        )
        ViewDefinitionMapper.createDefault(noteSegmentTypeHeaderDef, fields)

        val fieldsForNoteSegmentType = listOf(
            makeTextField("title", "Title", false),
            makeTextField("fontDesc", "Font", false),
            makeDateTimeField("createdDate", "Created", true)
        )
        val noteSegmentTypeDef = ViewDefinition(0, noteSegmentTypeHeaderDef.id,
            ViewDefConstants.noteSegmentTypeViewId,
            "Note Segment Type",
            2,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "NoteSegmentType",
            ""
        )

        ViewDefinitionMapper.createDefault(noteSegmentTypeDef, fieldsForNoteSegmentType)
        return noteSegmentTypeDef
    }

    private fun makeShelf() {
        val shelfDef = ViewDefinition(0, 0,
            ViewDefConstants.shelfViewId, "Shelf", 2, 1, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Shelf", ""/*,
            listOf(subjectDef)
            */
        )
        val name = makeTextField("title", "Title", true)
        val comments = makeMemoField("comments", "Comments", false)
        val createdDate = makeDateField("createdDate", "Created", false)
        val shelfFields = listOf(name, comments, createdDate)
        ViewDefinitionMapper.createDefault(shelfDef, shelfFields)

        val subjectDef = ViewDefinition(0, shelfDef.id,
            ViewDefConstants.subjectViewId, "Subject", 1, 2, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Subject", ""
        )
        val subjectFields = listOf(
            makeTextField("title", "Title", true),
            makeMemoField("comments", "Comments", false),
            makeDateField("createdDate", "Created", true)
        )
        ViewDefinitionMapper.createDefault(subjectDef, subjectFields)

        val publicationDef = ViewDefinition(0, subjectDef.id,
            ViewDefConstants.publicationViewId, "Publication", 1, 2, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Publication", ""
        )
        val publicationFields = listOf(
            makeTextField("title", "Title", true),
            makeLookupField("type", "Type", false, LookupUtils.publicationTypeLookupKey),
            makeMemoField("comments", "Comments", false),
            makeDateField("createdDate", "Created", true)
        )
        ViewDefinitionMapper.createDefault(publicationDef, publicationFields)

        val topicDef = ViewDefinition(0, publicationDef.id,
            ViewDefConstants.topicViewId, "Topic", 1, 2, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Topic", ""
        )
        val topicFields = listOf(
            makeTextField("name", "Name", true),
            makeMemoField("comments", "Comments", false),
            makeDateField("createdDate", "Created", true)
        )

        ViewDefinitionMapper.createDefault(topicDef, topicFields)

        /* this should have child of note segment */
        val noteDef = ViewDefinition(0, topicDef.id,
            ViewDefConstants.noteViewId, "Note", 1, 3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Note", ""
        )
        val noteFields   = listOf(
            makeTextField("title", "Title", true),
            makeMemoField("description", "Description", false),
            makeTextField("titleAudioFile", "Title Audio", false),
            makeTextField("descriptionAudioFile", "Description Audio", false),
            makeDateField("createdDate", "Created", true)
        )

        ViewDefinitionMapper.createDefault(noteDef, noteFields)


        val noteSegmentTypeDef = makeNoteSegmentTypeDef()
         val noteSegmentDef = ViewDefinition(0, noteDef.id,
             ViewDefConstants.noteSegmentViewId,
             "Note Segment", 1, 2,
             SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
             "NoteSegment", ""
         )
        val noteSegmentFields = listOf(
            makeReferenceField(
                "noteSegmentTypeId",
                "Note Segment Type",
                false,
                ReferenceDef(EntityDef(noteSegmentTypeDef.entityName))
            ),
            makeTextField("title", "Title", true),
            makeMemoField("body", "Body", true),
            makeTextField("bodyFile", "File", false),
            makeDateField("createdDate", "Created", false)
        )
        ViewDefinitionMapper.createDefault(noteSegmentDef, noteSegmentFields)

        val quizDef = ViewDefinition(0, publicationDef.id,
            ViewDefConstants.quizViewId, "Quiz", 1, 3, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Quiz", ""
        )
        val quizFields = listOf(
            makeTextField("name", "Name", true),
            makeReferenceField("topicId", "Topic", false, ReferenceDef(EntityDef(topicDef.entityName))),
            makeDateTimeField("createdDate", "Created", false)
        )
        ViewDefinitionMapper.createDefault(quizDef, quizFields)

        val questionDef = ViewDefinition(0, quizDef.id,
            ViewDefConstants.questionViewId, "Question", 1, 2, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Question", ""
        )
        val questionFields = listOf(
            makeMemoField("body", "Question", false),
            makeTextField("bodyFile", "Audio", false),
            makeDateTimeField("createdDate", "Created", false)
        )
        ViewDefinitionMapper.createDefault(questionDef, questionFields)

        val answerDef = ViewDefinition(0, questionDef.id,
            ViewDefConstants.answerViewId, "Answer", 1, 2, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "Answer", ""
        )
        val answerFields = listOf(
            makeMemoField("body", "Answer", false),
            makeTextField("bodyFile", "Audio", false),
            makeIntField("answerType", "Type", false),
            makeDateTimeField("createdDate", "Created", false)
        )
        ViewDefinitionMapper.createDefault(answerDef, answerFields)

    }

    private fun makePerson() {
        val name = makeTextField("name", "First Name", true)
        val income = makeMoneyField("income", "Income", true)
        val height = makeFloatField("height", "Height", true)
        val age = makeIntField("age", "Age", true)
        val country = makeLookupField("country", "Country", true, LookupUtils.countryLookupKey)
        val enteredDate = makeDateTimeField("enteredDate", "Entered", true)
        val isDeceased = makeBooleanField("deceased", "Deceased", true)
        val fields = listOf(name, income, height, age, country, enteredDate, isDeceased)
        val viewDef = ViewDefinition(
            0,
            0,
            ViewDefConstants.personViewId,
            "People",
            1,
            3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "person",
            ""
        )

        /* save the data to tables here */
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (viewDetail, viewDetailFields) = makePersonDetail(viewDef.id)
        ViewDefinitionMapper.createDefault(viewDetail, viewDetailFields)
    }

    private fun makePersonDetail(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val nickname = makeTextField("nickname", "Nickname", true)
        val petSpecies = makeLookupField("petSpecies", "Pet", true, LookupUtils.speciesLookupKey)
        val fields = listOf(nickname, petSpecies)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.personDetailsViewId,
            "Person Details",
            1,
            3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "persondetail",
            ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeLookupDetail(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val code = makeTextField("code", "Code", true)
        val label = makeTextField("label", "Label", true)
        val fields = listOf(code, label)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.lookupDetailViewId, "Lookup Items", 3, 1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "lookupdetail", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeLookup() {
        val key = makeTextField("key", "Key", true, filterable = true)
        val label = makeTextField("label", "Label", true, filterable = true)
        val encrypted = makeBooleanField("encrypted", "Encrypted", true)
        val fields = listOf(key, label, encrypted)
        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.lookupViewId,
            "Lookups",
            3,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "lookup",
            ""
        )
        /* save the data to tables here */
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (viewDetail, viewDetailFields) = makeLookupDetail(viewDef.id)
        ViewDefinitionMapper.createDefault(viewDetail, viewDetailFields)
    }


    private fun makeSnippets() {
        val name = makeTextField("name", "Name", true)
        val language =
            makeLookupField("language", "Language", true, LookupUtils.techLanguageLookupKey)
        val category = makeLookupField("category", "Category", true, LookupUtils.snippetCategoryKey)
        val topic = makeLookupField("topic", "Topic", true, LookupUtils.snippetTopicKey)
        val type = makeLookupField("type", "Type", true, LookupUtils.snippetTypeKey)
        val desc = makeMemoField("desc", "Description", false)
        val body = makeSourceField("body", "Body", false)
        val output = makeMemoField("output", "Output", false)
        val fields = listOf(name, language, category, topic, type, desc, body, output)

        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.techSnippetsViewId,
            "Snippets",
            1,
            5,
            SashOrientationDef.mappedOrientation(SashOrientationDef.HORIZONTAL),
            "snippet", "'"
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
    }


    private fun makeLogins() {
        val name = makeTextField("name", "Name", true, filterable = true)
        val category = makeLookupField("category", "Category", true, LookupUtils.loginCategoryKey)
        val userName = makeTextField("userName", "User Name", true)
        val password = makeLookupField("password", "Password", true, LookupUtils.passwordMasterKey)
        val url = makeTextField("url", "URL", false)
        val notes = makeMemoField("notes", "Notes", false)
        val other = makeMemoField("other", "Other", false)

        val fields = listOf(name, category, userName, password, url, notes, other)

        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.loginViewId,
            "Login",
            1,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "login", ""
        )

        ViewDefinitionMapper.createDefault(viewDef, fields)
    }


    private fun makeNotebooks() {
        val name = makeTextField("name", "Name", true, filterable = true)
        val comments = makeMemoField("comments", "Comments", false)

        val fields = listOf(name, comments)

        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.notebookViewId,
            "Notebooks", 3, 1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "notebook", ""
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (headerViewDef, headerFields) = makeNoteHeaders(viewDef.id)
        ViewDefinitionMapper.createDefault(headerViewDef, headerFields)
        val (detailViewDef, detailFields) = makeNoteDetails(headerViewDef.id)
        ViewDefinitionMapper.createDefault(detailViewDef, detailFields)
    }

    private fun makeNoteHeaders(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val name = makeTextField("name", "Name", true, filterable = true)
        val comments = makeMemoField("comments", "Comments", false)
        val fields = listOf(name, comments)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.noteheaderViewId,
            "Note Header",
            3,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "noteheader", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeNoteDetails(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val name = makeTextField("name", "Name", true)
        val body = makeMemoField("body", "Body", false)
        val sourceCode = makeMemoField("sourceCode", "Source", false)
        val comments = makeMemoField("comments", "Comments", false)
        val fields = listOf(name, body, sourceCode, comments)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.noteDetailViewId,
            "Note Detail",
            1,
            3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "notedetail", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeIngredients(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val name = makeTextField("name", "Name", true)
        val quantity = makeFloatField("quantity", "Quantity", true)
        val unit = makeLookupField("unit", "Unit", true, LookupUtils.unitLookupKey)
        val fields = listOf(name, quantity, unit)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.ingredientViewId,
            "Ingredients", 3, 1, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "ingredient", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeRecipes() {
        val name = makeTextField("name", "Name", true, filterable = true)
        val category = makeLookupField("category", "Category", true, LookupUtils.recipeCategoryLookupKey)
        val method = makeMemoField("method", "Method", true)
        val fields = listOf(name, category, method)
        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.recipeViewId, "Recipe", 1, 3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "recipe", ""
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (childViewDef, childFields) = makeIngredients(viewDef.id)
        ViewDefinitionMapper.createDefault(childViewDef, childFields)

    }

    private fun makeMenuItems(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>> {
        val text = makeTextField("text", "Text", true)
        val tabCaption = makeTextField("tabCaption", "Tab Caption", true)
        val modifierKey = makeLookupField("modifierKey", "Modifier Key", false, LookupUtils.modifierKeyLookupKey)
        val acceleratorKey = makeTextField("acceleratorKey", "Accelerator Key", false)
        val viewId = makeTextField( "viewId", "View", false)
        val scriptPath = makeTextField("scriptPath", "Script Path", false)
        val font = makeLookupField("font", "Font", false, LookupUtils.fontLookupKey)
        val image = makeLookupField("image", "Image", false, LookupUtils.imageLookupKey)
        val fields = listOf(text, tabCaption, modifierKey, acceleratorKey, viewId, scriptPath, font, image)
        val viewDef = ViewDefinition(
            0, parentViewId,
            ViewDefConstants.menuitemViewId,
            "Menu Items", 1, 3, SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "menuitem", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeMenus() {
        val text = makeTextField("text", "Text", required = true, filterable = false)
        val fields = listOf(text)
        val viewDef = ViewDefinition(
            0, 0,
            ViewDefConstants.menuManagerViewId, "Menu", 3, 1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "menu", ""
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (childViewDef, childFields) = makeMenuItems(viewDef.id)
        ViewDefinitionMapper.createDefault(childViewDef, childFields)
    }


    private fun makeFieldDefinition(parentViewId: Long): Pair<ViewDefinition, List<FieldDefinition>>{
        /* fields
        name: String,
        title: String,
        required: Boolean,
        size: SizeDef,
        dataType: DataTypeDef,
        lookupKey: String?,
        filterable: Boolean = false
         */
        val name = makeTextField("name", "Name", true)
        val title = makeTextField("title", "Title", true)
        val required = makeBooleanField("required", "Required", true)
        val size = makeLookupField("size", "Size", true, LookupUtils.fieldSizeLookupKey)
        val dataType = makeLookupField("dataType", "Data Type", true, LookupUtils.dataTypeLookupKey)
        //val lookupKey = makeTextField("lookupKey", "Lookup Key", false, false)
        val lookupKey =
            makeReferenceField("lookupKey", "Lookup", false, ReferenceDef(EntityDef("lookup")))
        val filterable = makeBooleanField("filterable", "Filter", false)
        val default = makeTextField("default", "Default", false)

        val fields = listOf(name, title, required, size, dataType, lookupKey, filterable, default)
        val viewDef = ViewDefinition(0, parentViewId,
            ViewDefConstants.fieldDefinitionViewId,
            "Field Definitions",
            1,
            3,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "FieldDefinition", ""
        )
        return Pair(viewDef, fields)
    }

    private fun makeAssociationDefinition(){
        val name = makeTextField("name", "Name", true)
        val owningEntity = makeTextField("owningEntity", "Owning Entity", true)
        val ownedEntity = makeTextField("ownedEntity", "Owned Entity", true)
        val owningType =
            makeLookupField(
                "owningType",
                "Owning Type",
                true,
                LookupUtils.associationTypeLookupKey
            )
        val ownedType =
            makeLookupField(
                "ownedType",
                "Owned Type",
                true,
                LookupUtils.associationTypeLookupKey
            )


        val junctionEntityName = makeTextField("junctionEntityName", "Junction Entity Name", false)

        val fields = listOf(name, owningEntity, ownedEntity, owningType, ownedType, junctionEntityName)

        val viewDef = ViewDefinition(0, 0,
            ViewDefConstants.associationViewId,
            "Associations",
            1,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "associationDefinition", ""
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
    }

    private fun makeViewDefinition() {
        /* fields are
        viewId : string
        title : string
        listWeight : int
        editWeight : int
        sashOrientation : string
        fieldDefintions : list
        entityDef : string
        childViews : list<ViewDef>
         */
        val viewId = makeTextField("viewId", "View ID", true, filterable = true)
        val title = makeTextField("title", "Title", true, filterable = true)
        val listWeight = makeIntField("listWeight", "List Weight", true)
        val editWeight = makeIntField("editWeight", "Edit Weight", true)
        val sashOrientation =
            makeLookupField(
                "sashOrientation",
                "Orientation",
                true,
                LookupUtils.sashOrientationLookupKey
            )
        val entityName = makeTextField("entityName", "Entity Name", true, filterable = false)

        val fields = listOf(viewId, title, listWeight, editWeight, sashOrientation, entityName)

        val viewDef = ViewDefinition(0, 0,
            ViewDefConstants.viewDefinitionViewId,
            "View Definitions",
            1,
            1,
            SashOrientationDef.mappedOrientation(SashOrientationDef.VERTICAL),
            "ViewDefinition", ""
        )
        ViewDefinitionMapper.createDefault(viewDef, fields)
        val (childViewDef, childFields) = makeFieldDefinition(viewDef.id)
        ViewDefinitionMapper.createDefault(childViewDef, childFields)
    }


    fun makeDefaultViews() {
        makePerson()
        makeLookup()
        makeLogins()
        makeSnippets()
        makeNotebooks()
        makeRecipes()
        makeViewDefinition()
        makeShelf()
        makePasswordForm()
        makeMenus()
        makeAssociationDefinition()
    }

    fun loadView(viewId: String, loadChildren: Boolean = true): ViewDef {
        //todo - add a cache and reload function
        val views: List<ViewDefinition> = ViewDefinitionMapper.getByViewId(viewId);
        val viewDefs = views.map{
            val fields = FieldDefinitionMapper.getAll(mapOf("viewDefinitionId" to it.id))
            if(loadChildren) {
                val childViews = ApplicationData.loadChildViews(it.id)
               ApplicationData.mapViewDefinitionToViewDef(it, fields, childViews)
            } else {
               ApplicationData.mapViewDefinitionToViewDef(it, fields, listOf())
            }
        }
        return viewDefs.get(0)
    }
}

/*
object ViewDefinitions {

    fun load(): String {
        val viewDefPath = ApplicationData.userPath + File.separator + ApplicationData.viewDefinitionsFile
        val viewDefFile = File(viewDefPath)
        return loadViewDefs(viewDefFile)
    }

    private fun loadViewDefs(viewDefFile: File): String {
        if (!viewDefFile.exists()) {
            // seed the file with defaults
            val defaultViewDefs = getEncoded();
            viewDefFile.writeText(defaultViewDefs)
            return defaultViewDefs
        } else {
            return viewDefFile.readText()
        }
    }

    private fun getEncoded(): String {
        val format = Json { prettyPrint = true }
        return format.encodeToString(makeViews())
    }

    fun makeViews(): List<ViewDef> = listOf(
        makeLogins(),
        makeSnippets(),
        makeFauxRecipes()
    ) + makeRecipes() + makeNotebooks() + makePerson() + makeShelf() + makeLookup() + makeViewDefinition()

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

    private fun makeDateField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.DATE, null)
    }


    private fun makeMoneyField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.MEDIUM, DataTypeDef.MONEY, null)
    }

    private fun makeBooleanField(name: String, title: String, required: Boolean): FieldDef {
        return makeField(name, title, required, SizeDef.SMALL, DataTypeDef.BOOLEAN, null)
    }

    private fun makeReferenceField(
        name: String,
        title: String,
        required: Boolean,
        referenceDef: ReferenceDef
    ): FieldDef {
        return FieldDef(name, title, required, SizeDef.SMALL, DataTypeDef.REFERENCE, null, false, "", referenceDef)
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
        val noteSegmentTypeDef = ViewDef(
            ViewDefConstants.noteSegmentTypeViewId,
            "Note Segment Type", 2, 1, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", false),
                makeTextField("fontDesc", "Font", false),
                makeDateTimeField("createdDate", "Created", true)
            ),
            EntityDef("NoteSegmentType")
        )
        val noteSegmentTypeHeaderDef = ViewDef(
            ViewDefConstants.noteSegmentTypeHeaderViewId,
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
        val createdDate = makeDateField("createdDate", "Created", false)


        val noteSegmentsType = makeNoteSegmentTypeDef()
        val noteSegmentTypeDef = noteSegmentsType[1]

        val noteSegmentDef = ViewDef(
            ViewDefConstants.noteSegmentViewId, "Note Segment", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeReferenceField(
                    "noteSegmentTypeId",
                    "Note Segment Type",
                    false,
                    ReferenceDef(noteSegmentTypeDef.entityDef)
                ),
                makeTextField("title", "Title", true),
                makeMemoField("body", "Body", true),
                makeTextField("bodyFile", "File", false),
                makeDateField("createdDate", "Created", false)
            ),
            EntityDef("NoteSegment"),
            listOf()
        )

        val noteDef = ViewDef(
            ViewDefConstants.noteViewId, "Note", 1, 3, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", true),
                makeMemoField("description", "Description", false),
                makeTextField("titleAudioFile", "Title Audio", false),
                makeTextField("descriptionAudioFile", "Description Audio", false),
                makeDateField("createdDate", "Created", true)
            ),
            EntityDef("Note"),
            listOf(noteSegmentDef)
        )


        val topicDef = ViewDef(
            ViewDefConstants.topicViewId, "Topic", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("name", "Name", true),
                makeMemoField("comments", "Comments", false),
                makeDateField("createdDate", "Created", true)
            ),
            EntityDef("Topic"),
            listOf(noteDef)
        )

        val answerDef = ViewDef(
            ViewDefConstants.answerViewId, "Answer", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeMemoField("body", "Answer", false),
                makeTextField("bodyFile", "Audio", false),
                makeIntField("answerType", "Type", false),
                makeDateTimeField("createdDate", "Created", false)
            ),
            EntityDef("Answer"),
            listOf()
        )

        val questionDef = ViewDef(
            ViewDefConstants.questionViewId, "Question", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeMemoField("body", "Question", false),
                makeTextField("bodyFile", "Audio", false),
                makeDateTimeField("createdDate", "Created", false)
            ),
            EntityDef("Question"),
            listOf(answerDef)
        )

        val quizDef = ViewDef(
            ViewDefConstants.quizViewId, "Quiz", 1, 3, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("name", "Name", true),
                makeReferenceField("topicId", "Topic", false, ReferenceDef(topicDef.entityDef)),
                makeDateTimeField("createdDate", "Created", false)
            ),
            EntityDef("Quiz"),
            listOf(questionDef)
        )

        val publicationDef = ViewDef(
            ViewDefConstants.publicationViewId, "Publication", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", true),
                makeLookupField("type", "Type", false, publicationTypeLookupKey),
                makeMemoField("comments", "Comments", false),
                makeDateField("createdDate", "Created", true)
            ),
            EntityDef("Publication"),
            listOf(topicDef, quizDef)
        )

        val subjectDef = ViewDef(
            ViewDefConstants.subjectViewId, "Subject", 1, 2, SashOrientationDef.VERTICAL,
            listOf(
                makeTextField("title", "Title", true),
                makeMemoField("comments", "Comments", false),
                makeDateField("createdDate", "Created", true)
            ),
            EntityDef("Subject"),
            listOf(publicationDef)
        )

        val view = ViewDef(
            ViewDefConstants.shelfViewId, "Shelf", 2, 1, SashOrientationDef.VERTICAL,
            listOf(name, comments, createdDate),
            EntityDef("Shelf"),
            listOf(subjectDef)
        )
        return listOf(
            view,
            subjectDef,
            publicationDef,
            topicDef,
            noteDef,
            noteSegmentDef,
            questionDef,
            answerDef,
            quizDef
        ) + noteSegmentsType
    }

    private fun makeLookupDetail(): ViewDef {
        val code = makeTextField("code", "Code", true)
        val label = makeTextField("label", "Label", true)
        return ViewDef(
            ViewDefConstants.lookupDetailViewId, "Lookup Items", 1, 3, SashOrientationDef.VERTICAL,
            listOf(code, label), EntityDef("lookupdetail"), emptyList()
        )
    }

    private fun makeLookup(): List<ViewDef> {
        val key = makeTextField("key", "Key", true, filterable = true)
        val label = makeTextField("label", "Label", true, filterable = true)
        val encrypted = makeBooleanField("encrypted", "Encrypted", true)
        val lookupDetailDef = makeLookupDetail()
        val view = ViewDef(
            ViewDefConstants.lookupViewId,
            "Lookups",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(key, label, encrypted),
            EntityDef("lookup"),
            listOf(lookupDetailDef)
        )
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
        val view = ViewDef(
            ViewDefConstants.personViewId, "People", 1, 3, SashOrientationDef.VERTICAL,
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
            ViewDefConstants.personDetailsViewId, "Person Details", 1, 3, SashOrientationDef.VERTICAL,
            listOf(nickname, petSpecies), EntityDef("persondetail"), listOf()
        )
    }

    private fun makeIngredients(): ViewDef {
        val name = makeTextField("name", "Name", true)
        val quantity = makeFloatField("quantity", "Quantity", true)
        val unit = makeLookupField("unit", "Unit", true, ApplicationData.unitLookupKey)
        return ViewDef(
            ViewDefConstants.ingredientViewId,
            "Ingredients", 1, 3, SashOrientationDef.HORIZONTAL,
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
        return listOf(
            ViewDef(
                ViewDefConstants.recipeViewId, "Recipe", 1, 5, SashOrientationDef.HORIZONTAL,
                listOf(name, category, method),
                EntityDef("recipe"),
                listOf(ingredients)
            ), ingredients
        )

    }

    private fun makeFauxRecipes(): ViewDef {
        val name = makeTextField("name", "Name", true, filterable = true)
        val category = makeLookupField("category", "Category", true, ApplicationData.recipeCategoryLookupKey)
        val method = makeMemoField("method", "Method", true)
        return ViewDef(
            ViewDefConstants.fauxRecipeViewId, "Recipe", 1, 5, SashOrientationDef.HORIZONTAL,
            listOf(name, category, method),
            EntityDef("recipe")
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
            ViewDefConstants.techSnippetsViewId,
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
        val name = makeTextField("name", "Name", true, filterable = true)
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
            1,
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

    private fun makeFieldDefinition(): ViewDef {
        /* fields
        name: String,
        title: String,
        required: Boolean,
        size: SizeDef,
        dataType: DataTypeDef,
        lookupKey: String?,
        filterable: Boolean = false
         */
        val name = makeTextField("name", "Name", true)
        val title = makeTextField("title", "Title", true)
        val required = makeBooleanField("required", "Required", true)
        val size = makeLookupField("size", "Size", true, ApplicationData.fieldSizeLookupKey)
        val dataType = makeLookupField("dataType", "Data Type", true, ApplicationData.dataTypeLookupKey)
        //val lookupKey = makeTextField("lookupKey", "Lookup Key", false, false)
        val lookupKey = makeReferenceField("lookupKey", "Lookup", false, ReferenceDef(EntityDef("lookup")))
        val filterable = makeBooleanField("filterable", "Filter", false)
        val default = makeTextField("default", "Default", false)

        return ViewDef(
            ViewDefConstants.fieldDefinitionViewId,
            "Field Definitions",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(name, title, required, size, dataType, lookupKey, filterable, default),
            EntityDef("FieldDefinition"), emptyList()
        )
    }

    private fun makeViewDefinition(): List<ViewDef> {
        /* fields are
        viewId : string
        title : string
        listWeight : int
        editWeight : int
        sashOrientation : string
        fieldDefintions : list
        entityDef : string
        childViews : list<ViewDef>
         */
        val viewId = makeTextField("viewId", "View ID", true, filterable = true)
        val title = makeTextField("title", "Title", true, filterable = true)
        val listWeight = makeIntField("listWeight", "List Weight", true)
        val editWeight = makeIntField("editWeight", "Edit Weight", true)
        val sashOrientation =
            makeLookupField("sashOrientation", "Orientation", true, LookupUtils.sashOrientationLookupKey)
        val entityName = makeTextField("entityName", "Entity Name", true, filterable = false)

        val fieldDefinitionDef = makeFieldDefinition()
        val view = ViewDef(
            ViewDefConstants.viewDefinitionViewId,
            "View Definitions",
            1,
            3,
            SashOrientationDef.VERTICAL,
            listOf(viewId, title, listWeight, editWeight, sashOrientation, entityName),
            EntityDef("ViewDefinition"),
            listOf(fieldDefinitionDef),
            true
        )
        return listOf(view, fieldDefinitionDef)
    }

}
 */