package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class Snippet(
    override var id: Long = 0,
    name: String,
    language: String,
    category: String,
    topic: String,
    type: String,
    desc: String,
    body: String
) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var language: String by Delegates.observable(language, observer)
    var category: String by Delegates.observable(category, observer)
    var topic: String by Delegates.observable(topic, observer)
    var type: String by Delegates.observable(type, observer)
    var desc: String by Delegates.observable(desc, observer)
    var body: String by Delegates.observable(body, observer)
    /*
    var output: String by Delegates.observable(output, observer)
    var canRun: Boolean by Delegates.observable(canRun, observer)

     */

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> "${ApplicationData.techLanguage.find { it.code == language }?.label}"
            2 -> "${ApplicationData.snippetCategory.find { it.code == category }?.label}"
            3 -> "${ApplicationData.snippetTopic.find { it.code == topic }?.label}"
            4 -> "${ApplicationData.snippetType.find { it.code == type }?.label}"
            else -> ""
        }
    }


    override fun toString(): String {
        //return "Snippets(id=$id, name=$name, language=$language, category=$category, topic=$topic, type=$type, desc=$desc, output=$output, canRun=$canRun)"
        return "Snippets(id=$id, name=$name, language=$language, category=$category, topic=$topic, type=$type, desc=$desc)"
    }

    companion object Factory {
        fun make(): Snippet {
            return Snippet(
                    0,
                    "",
                    ApplicationData.techLanguage[0].code,
                    ApplicationData.snippetCategory[0].code,
                    ApplicationData.snippetTopic[0].code,
                    ApplicationData.snippetType[0].code,
                    "",
                    ""
            )
        }
    }
}