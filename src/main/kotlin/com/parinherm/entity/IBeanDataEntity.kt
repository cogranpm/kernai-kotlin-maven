package com.parinherm.entity

interface IBeanDataEntity {
    fun make() : ModelObject
    val data: List<ModelObject>
}