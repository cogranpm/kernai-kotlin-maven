package com.parinherm.entity.schema.custom

import org.jetbrains.exposed.sql.BlobColumnType
import org.jetbrains.exposed.sql.IColumnType

class LongBlobColumnType : IColumnType by BlobColumnType() {
    override fun sqlType(): String = "LONGBLOB"
}