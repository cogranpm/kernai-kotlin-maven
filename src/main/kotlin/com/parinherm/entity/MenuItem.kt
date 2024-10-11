package com.parinherm.entity

import com.parinherm.ApplicationData
import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates

class MenuItem(override var id: Long = 0, var menuId: Long, text: String, tabCaption: String, modifierKey: String, acceleratorKey: String, viewId: String, scriptPath: String, font: String, image: String): ModelObject(), IBeanDataEntity  {

    var text: String by Delegates.observable(text, observer)
    var tabCaption: String by Delegates.observable(tabCaption, observer)
    var modifierKey: String by Delegates.observable(modifierKey, observer)
    var acceleratorKey: String by Delegates.observable(acceleratorKey, observer)
    var viewId: String by Delegates.observable(viewId, observer)
    var scriptPath: String by Delegates.observable(scriptPath, observer)
    var font: String by Delegates.observable(font, observer)
    var image: String by Delegates.observable(image, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> text
             1 -> tabCaption
             2 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.modifierKeyLookupKey, true).find { it.code == modifierKey}
                "${listItem?.label}"
            }
             3 -> acceleratorKey
             4 -> viewId
             5 -> scriptPath
             6 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.fontLookupKey, true).find { it.code == font}
                "${listItem?.label}"
            }
             7 ->  {
                val listItem = LookupUtils.getLookupByKey(LookupUtils.imageLookupKey, true).find { it.code == image}
                "${listItem?.label}"
            }
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "MenuItem(id=$id, text=$text, tabCaption=$tabCaption, modifierKey=$modifierKey, acceleratorKey=$acceleratorKey, viewId=$viewId, scriptPath=$scriptPath, font=$font, image=$image)"
    }

    companion object Factory {
        fun make(MenuId: Long): MenuItem{
            return MenuItem(
                0,
                MenuId,
                 "" , 
                 "" ,
                LookupUtils.getLookupByKey(LookupUtils.modifierKeyLookupKey, true)[0].code,
                 "" , 
                 "" , 
                 "" ,
                LookupUtils.getLookupByKey(LookupUtils.fontLookupKey, true)[0].code ,
                LookupUtils.getLookupByKey(LookupUtils.imageLookupKey, true)[0].code
            )
        }
    }
}
