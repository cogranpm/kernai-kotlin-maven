package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class Login(
    override var id: Long = 0,
    name: String,
    category: String,
    userName: String,
    password: String,
    url: String,
    notes: String,
    other: String
) : ModelObject(), IBeanDataEntity {

    val name: String by Delegates.observable(name, observer)
    val category: String by Delegates.observable(category, observer)
    var userName: String by Delegates.observable(userName, observer)
    var password: String by Delegates.observable(password, observer)
    var url: String by Delegates.observable(url, observer)
    var notes: String by Delegates.observable(notes, observer)
    var other: String by Delegates.observable(other, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> "${ApplicationData.loginCategoryList.find { it.code == category}?.code}"
            2 -> userName
            3 -> password
            else -> ""
        }
    }


    override fun toString(): String {
        return "Snippets(id=$id, name=$name category=$category, userName=$userName, url=$url)"
    }

    companion object Factory {
        fun make(): Login{
            return Login(
                0,
                "",
                ApplicationData.loginCategoryList[0].code,
                "",
                "",
                "",
                "",
                ""
            )
        }
    }
}