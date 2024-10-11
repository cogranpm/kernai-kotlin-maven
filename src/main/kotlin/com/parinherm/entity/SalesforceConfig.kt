package com.parinherm.entity

import com.parinherm.lookups.LookupUtils
import kotlin.properties.Delegates

class SalesforceConfig
    (override var id: Long = 0, name: String, salesforceUrl: String, loginToken: String ): ModelObject(), IBeanDataEntity {

    var name: String by Delegates.observable(name, observer)
    var salesforceUrl: String by Delegates.observable(salesforceUrl, observer)
    var loginToken:  String by Delegates.observable(loginToken, observer)

    override fun getColumnValueByIndex(index: Int): String {
        return when (index) {
            0 -> name
            1 -> salesforceUrl
            2 -> loginToken
            else -> ""
        }
    }
    override fun toString(): String {
       return "SalesforceConfig(id=$id, name=$name, salesforceUrl=$salesforceUrl, loginToken=$loginToken)";
    }

}