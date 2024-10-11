package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.lookups.LookupUtils
import com.parinherm.security.Cryptographer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


object LookupMapper : IMapper<Lookup> {

    val table = Lookups

    fun mapItem(item: Lookup, statement: UpdateBuilder<Int>): Unit {
        statement[table.key] = item.key
        statement[table.label] = item.label
        statement[table.encrypted] = item.encrypted
    }

    /*
    fun getLookups(): MutableMap<String, List<LookupDetail>> {
        var lookups = mutableMapOf<String, List<LookupDetail>>()
        transaction {
            val query = table.join(LookupDetails, JoinType.INNER)
                .slice(table.key, table.encrypted, LookupDetails.id, LookupDetails.lookupId, LookupDetails.code, LookupDetails.label)
                .selectAll()
            query.orderBy(table.key to SortOrder.ASC)
            lookups = query.map {
                Pair(
                    it[table.key], LookupDetailMapper.processLookupDetail(it[table.encrypted], LookupDetail(
                        it[LookupDetails.id].value,
                        it[LookupDetails.lookupId],
                        it[LookupDetails.code],
                        it[LookupDetails.label]
                    ))
                )
            }.groupBy(
                { it.first },
                { it.second }
            ) as MutableMap<String, List<LookupDetail>>
        }
        return lookups
    }
     */

    fun getLookup(lookupId: Long?): MutableMap<String, List<LookupDetail>> {
        var lookups = mutableMapOf<String, List<LookupDetail>>()
        transaction {
            val query = table.join(LookupDetails, JoinType.INNER)
                .slice(table.key, table.encrypted, LookupDetails.id, LookupDetails.lookupId, LookupDetails.code, LookupDetails.label)
                .select { if(lookupId != null) table.id eq lookupId else table.id neq null}
            query.orderBy(table.key to SortOrder.ASC)
            lookups = query.map {
                Pair(
                    it[table.key], LookupDetailMapper.processLookupDetail(it[table.encrypted], LookupDetail(
                        it[LookupDetails.id].value,
                        it[LookupDetails.lookupId],
                        it[LookupDetails.code],
                        it[LookupDetails.label]
                    ))
                )
            }.groupBy(
                { it.first },
                { it.second }
            ) as MutableMap<String, List<LookupDetail>>
        }
       return lookups
    }

    override fun save(item: Lookup) {
        MapperHelper.save(item, table, ::mapItem)
    }

    override fun getAll(keys: Map<String, Long>): List<Lookup> {
        return MapperHelper.getAll(keys, table, null, table.label to SortOrder.ASC) {
            Lookup(
                it[table.id].value,
                it[table.key],
                it[table.label],
                it[table.encrypted]
            )
        }
    }

    fun getAllKeys(): List<String> =
        transaction {
            val query = table.slice(table.key).selectAll()
            query.orderBy(table.key to SortOrder.ASC)
            return@transaction query.map {
                it[table.key]
            }
        }

    override fun delete(item: Lookup) {
        LookupDetailMapper.delete(item)
        MapperHelper.delete(table, table.id eq item.id)
    }

    fun getByKey(key: String): List<Lookup> {
        return MapperHelper.getAll(
            mapOf("" to 0),
            table, table.key eq key, table.label to SortOrder.ASC
        ) {
            Lookup(
                it[table.id].value,
                it[table.key],
                it[table.label],
                it[table.encrypted]
            )
        }
    }

    fun isEncrypted(id: Long): Boolean {
        val lookups = MapperHelper.getAll(
            mapOf("" to 0),
            table, table.id eq id, table.label to SortOrder.ASC
        ) {
            Lookup(
                it[table.id].value,
                it[table.key],
                it[table.label],
                it[table.encrypted]
            )
        }
        return if(lookups.isEmpty()){
            false
        } else {
            lookups[0].encrypted
        }
    }

    /* this only works going from unecrypted to encrypted
    * not from 1 secret key to another
    */
    fun encryptAllRows(key: String){
        val lookups = getByKey(key)
        if(lookups.isEmpty()){
            return
        }

        val lookup = lookups[0]
        val lookupDetails = LookupDetailMapper.getAll(mapOf("lookupId" to lookup.id))
        lookupDetails.forEach {
            val encrypted = Cryptographer.encrypt(it.code)
            val decrypted = Cryptographer.decrypt(encrypted)
            it.code = decrypted
            LookupDetailMapper.save(it)
        }
    }

}