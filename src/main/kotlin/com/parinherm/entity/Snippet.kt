package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class Snippet(override var id: Long = 0,
              name: String,
              language: String,
              desc: String,
              body: String) : ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var language: String by Delegates.observable(language, observer)
    var desc: String by Delegates.observable(desc, observer)
    var body: String by Delegates.observable(body, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> "${ApplicationData.techLanguage.find { it.code == language}?.code}"
            else -> ""
        }
    }



    override fun toString(): String {
        return "Snippets(id=$id, name=$name, language=$language, desc=$desc)"
    }

    companion object Factory {
        fun make(): Snippet{
            return Snippet(0, "", ApplicationData.techLanguage[0].code, "", "")
        }
    }
}