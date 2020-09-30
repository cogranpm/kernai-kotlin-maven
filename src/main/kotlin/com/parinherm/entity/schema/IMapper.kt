package com.parinherm.entity.schema

interface IMapper<T> {
    fun save(item: T) : Unit
    fun getAll(keys: Map<String, Long>) : List<T>
}