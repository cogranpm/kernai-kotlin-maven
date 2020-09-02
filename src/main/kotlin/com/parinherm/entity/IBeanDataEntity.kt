package com.parinherm.entity

interface IBeanDataEntity {
    var id: Long
    fun getColumnValueByIndex(index: Int) : String
}