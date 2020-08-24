package com.parinherm.entity

import org.eclipse.core.databinding.observable.map.WritableMap

interface IDataEntity {
    val data: List<WritableMap<String, Any>>
    fun make(): WritableMap<String, Any>
}