package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class LookupDetail (override var id: Long = 0, var lookupId: Long, code: String, label: String): ModelObject(), IBeanDataEntity  {
    var code: String by Delegates.observable(code, observer)
    var label: String by Delegates.observable(label, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> code
            1 -> label
           else -> ""
        }
    }


    override fun toString(): String {
        return "LookupDetail(id=$id, lookupId:$lookupId, code=$code, label=$label)"
    }

    companion object Factory {
        fun make(lookupId: Long): LookupDetail{
            return LookupDetail(
                0,
                lookupId,
                "",
                ""
           )
        }
    }
}