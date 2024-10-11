package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
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

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> "${LookupUtils.getLookupByKey(LookupUtils.techLanguageLookupKey, false).find { it.code == language }?.label}"
            2 -> "${LookupUtils.getLookupByKey(LookupUtils.snippetCategoryKey, false).find { it.code == category }?.label}"
            3 -> "${LookupUtils.getLookupByKey(LookupUtils.snippetTopicKey, false).find { it.code == topic }?.label}"
            4 -> "${LookupUtils.getLookupByKey(LookupUtils.snippetTypeKey, false).find { it.code == type }?.label}"
            else -> ""
        }
    }


    override fun toString(): String {
        //return "Snippets(id=$id, name=$name, language=$language, category=$category, topic=$topic, type=$type, desc=$desc, output=$output, canRun=$canRun)"
        return "Snippets(id=$id, name=$name, language=$language, category=$category, topic=$topic, type=$type, desc=$desc, body=$body)"
    }

    companion object Factory {
        fun make(): Snippet {
            return Snippet(
                    0,
                    "",
                LookupUtils.getLookupByKey(LookupUtils.techLanguageLookupKey, false)[0].code,
                LookupUtils.getLookupByKey(LookupUtils.snippetCategoryKey, false)[0].code,
                LookupUtils.getLookupByKey(LookupUtils.snippetTopicKey, false)[0].code,
                LookupUtils.getLookupByKey(LookupUtils.snippetTypeKey, false)[0].code,
                    "",
                    ""
            )
        }
    }
}