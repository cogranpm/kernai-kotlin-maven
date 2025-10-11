package com.parinherm.form.definitions

object ViewDefConstants {
    const val associationViewId = "associationDef"
    const val personViewId = "person"
    const val personDetailsViewId = "persondetails"
    const val recipeViewId = "recipe"
    const val fauxRecipeViewId = "fauxRecipe"
    const val ingredientViewId = "ingredients"
    const val techSnippetsViewId = "techsnip"
    const val loginViewId = "login"
    const val notebookViewId = "notebook"
    const val noteheaderViewId = "noteheader"
    const val noteDetailViewId = "notedetail"
    const val lookupViewId = "lookup"
    const val lookupDetailViewId = "lookupdetail"
    const val shelfViewId = "shelf"
    const val subjectViewId = "subject"
    const val topicViewId = "topic"
    const val publicationViewId = "publication"
    const val noteViewId = "note"
    const val noteSegmentViewId = "notesegment"
    const val noteSegmentTypeViewId = "notesegmenttype"
    const val noteSegmentTypeHeaderViewId = "notesegmenttypeheader"
    const val answerViewId = "answer"
    const val questionViewId = "question"
    const val quizViewId = "quiz"
    const val quizRunHeaderViewId = "quizrunheader"
    const val quizRunQuestionViewId = "quizrunquestion"
    const val viewDefinitionViewId = "viewdefinition"
    const val fieldDefinitionViewId = "fielddefinition"
    const val menuManagerViewId = "menumanager"
    const val menuitemViewId = "menuitem"
    const val passwordsViewId = "passwords"
    const val salesforceConfigViewId = "salesforceConfig"
    const val databaseConnectionViewId = "databaseConnection"

    // the properties available to the views
    const val title = "title"
    const val version = "version"
    const val viewid = "viewid"
    const val btnRemove = "btnRemove"
    const val btnAdd = "btnAdd"
    const val list = "list"
    const val tab = "tab"
    const val add_caption = "Add"

    const val forms = "forms"
    const val fields = "fields"

    //what field from the data (a map) is the input control binding to
    const val fieldName = "fieldName"

    // needed for conversions text to int etc
    //determines what control type is used
    const val fieldDataType = "fieldDataType"

    // possible datatypes
    const val float = "float"
    const val int = "int"
    const val text = "text"

    // memo is long text
    const val memo = "memo"
    const val lookup = "lookup"
    const val lookupKey = "lookupKey"
    const val bool = "bool"
    const val datetime = "datetime"
    const val money = "money"

    // source is a source code editor
    const val source = "source"

    // sizing
    const val sizeHint = "sizeHint"
    const val large = "large"
    const val medium = "medium"
    const val small = "small"

    const val fieldLabelConverter = "fieldLabelConverter"
    const val required = "required"
    const val listWeight = "listweight"
    const val editWeight = "editweight"
    const val sashOrientation = "sashorientation"
    const val horizontal = "horizontal"
    const val vertical = "vertical"

    const val childViews = "childViews"

    fun makeColumnMapKey(fieldName: String): String = fieldName + "_column"

}