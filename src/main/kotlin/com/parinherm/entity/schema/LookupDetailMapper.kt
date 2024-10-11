package com.parinherm.entity.schema

import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.security.Cryptographer
import org.jasypt.util.text.AES256TextEncryptor
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object LookupDetailMapper : IMapper<LookupDetail> {

    val table = LookupDetails


    fun mapItem(item: LookupDetail, statement: UpdateBuilder<Int>): Unit {
        statement[table.code] = item.code
        statement[table.label] = item.label
        statement[table.lookupId] = item.lookupId
    }

    override fun save(item: LookupDetail) {
        val unencryptedCode = item.code
        val encrypted = LookupMapper.isEncrypted(item.lookupId)
        if (encrypted) {
            item.code = Cryptographer.encrypt(item.code)
        }
        MapperHelper.save(item, table, ::mapItem)
        item.code = unencryptedCode
    }

    override fun getAll(keys: Map<String, Long>): List<LookupDetail> {
        val details = MapperHelper.getAll(
            keys, table,
            table.lookupId eq keys["lookupId"] as Long, table.label to SortOrder.ASC
        ) {
            LookupDetail(
                it[table.id].value,
                it[table.lookupId],
                it[table.code],
                it[table.label]
            )
        }
        return processLookupDetails(details)
    }

    fun getByLookupKey(keys: Map<String, String>): List<LookupDetail> {
        val lookup = LookupMapper.getByKey(keys["key"] as String)
        if(!lookup.isEmpty()){
            val details = MapperHelper.getAll(
                mapOf(), table,
                table.lookupId eq lookup[0].id, table.label to SortOrder.ASC
            ) {
                LookupDetail(
                    it[table.id].value,
                    it[table.lookupId],
                    it[table.code],
                    it[table.label]
                )
            }
            return processLookupDetails(details)
        } else {
            return listOf()
        }
    }


    private fun processLookupDetails(details: List<LookupDetail>): List<LookupDetail>{
        if(details.isEmpty()){
            return details
        }
        val firstDetail = details[0]
        if(LookupMapper.isEncrypted(firstDetail.lookupId)){
            return details.map {
                processLookupDetail(true, it)
            }
        } else {
            return details
        }
    }

    fun processLookupDetail(encrypted: Boolean, item: LookupDetail): LookupDetail {
        if(encrypted){
            val decryptedCode = Cryptographer.decrypt(item.code)
            return LookupDetail(item.id, item.lookupId, decryptedCode, item.label)
        } else {
            return item
        }
    }

    fun exists(lookupId: Long, code: String): Boolean {
        val list = MapperHelper.getAll(
            mapOf(), table,
            (table.lookupId eq lookupId) and (table.code eq code), table.label to SortOrder.ASC
        ) {
            LookupDetail(
                it[table.id].value,
                it[table.lookupId],
                it[table.code],
                it[table.label]
            )
        }
        return !list.isEmpty()
    }

    override fun delete(item: LookupDetail) {
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun delete(parent: Lookup) {
        MapperHelper.delete(table, table.lookupId eq parent.id)
    }

}