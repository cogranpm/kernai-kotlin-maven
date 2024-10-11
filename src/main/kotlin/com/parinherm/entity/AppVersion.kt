package com.parinherm.entity

import com.parinherm.ApplicationData
import kotlin.properties.Delegates

class AppVersion(override var id: Long = 0,  version: Double): ModelObject(), IBeanDataEntity  {

    var version: Double by Delegates.observable(version, observer)
    

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
             0 -> "$version"
            
            else -> ""
        }
    }

    override fun toString(): String {
        return "appVersion(id=$id, version=$version)"
    }

    companion object Factory {
        fun make(): AppVersion{
            return AppVersion(
                0,
                
                 0.0 
                
            )
        }
    }
}
