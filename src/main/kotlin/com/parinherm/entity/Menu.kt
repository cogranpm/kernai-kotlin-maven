package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class Menu(override var id: Long = 0,  text: String): ModelObject(), IBeanDataEntity  {

    var text: String by Delegates.observable(text, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> text
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "Menu(id=$id, text=$text)"
    }

    companion object Factory {
        fun make(): Menu{
            return Menu(
                0,
                
                 "" 
                
            )
        }
    }
}
