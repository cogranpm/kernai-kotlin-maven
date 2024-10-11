package com.parinherm.settings

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.dbKeyDriver
import com.parinherm.ApplicationData.dbKeyPassword
import com.parinherm.ApplicationData.dbKeyType
import com.parinherm.ApplicationData.dbKeyUrl
import com.parinherm.ApplicationData.dbKeyUser
import com.parinherm.ApplicationData.encryptionSecretKey
import com.parinherm.ApplicationData.propertiesFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.util.*

data class Setting(
    var dbType: String?,
    var dbUrl: String?,
    var dbDriver: String?,
    var dbUser: String?,
    var dbPassword: String?,
    var encryptionSecret: String?
) {

    val keys = listOf(dbKeyType, dbKeyUrl, dbKeyDriver, dbKeyUser, dbKeyPassword, encryptionSecretKey)

    fun read() {
        val allProps = readAll()
        dbType = allProps.getOrDefault(dbKeyType, "")
        dbUrl = allProps.getOrDefault(dbKeyUrl, "")
        dbDriver = allProps.getOrDefault(dbKeyDriver, "")
        dbUser = allProps.getOrDefault(dbKeyUser, "")
        dbPassword = allProps.getOrDefault(dbKeyPassword, "")
        encryptionSecret = allProps.getOrDefault(encryptionSecretKey, "")
    }

    fun write() {
        try {
            validate()
            val io = openFile()
            val props = Properties()
            FileInputStream(io).use {
                props.setProperty(dbKeyType, dbType ?: "")
                props.setProperty(dbKeyUrl, dbUrl ?: "")
                props.setProperty(dbKeyDriver, dbDriver ?: "")
                props.setProperty(dbKeyUser, dbUser ?: "")
                props.setProperty(dbKeyPassword, dbPassword ?: "")
                props.setProperty(encryptionSecretKey, encryptionSecret ?: "")
                val out: OutputStream = FileOutputStream(io)
                props.store(out, "")
            }
        } catch (e: Exception) {
            ApplicationData.logError(e, "Error in Settings Write ${this}")
            throw e
        }
    }


    fun validate() {
        EncryptionSecretValidator.validate(encryptionSecret)

        if (dbType.isNullOrBlank()) {
            throw SettingsValidationException("Database Type cannot be blank")
        }
        when (dbType) {
            ApplicationData.dbTypeMySql, ApplicationData.dbTypePostgres -> {
                if(dbUrl.isNullOrBlank()){
                    throw SettingsValidationException("Url cannot be empty")
                }
                if(dbUser.isNullOrBlank()){
                    throw SettingsValidationException("User cannot be empty")
                }
                if(dbDriver.isNullOrBlank()){
                    throw SettingsValidationException("Driver cannot be empty")
                }
            }
            ApplicationData.dbTypeEmbedded -> {
                //nothing to check here
            }
            ApplicationData.dbTypeSqlite -> {
                if(dbUrl.isNullOrBlank()){
                    throw SettingsValidationException("Url cannot be empty")
                }
            }
            else -> {
                throw SettingsValidationException("dbType:  ${dbType}, is not valid")
            }
        }
    }

    private fun readAll(): Map<String, String> {
        val props = FileInputStream(openFile()).use {
            Properties().apply { load(it) }
        }
        return keys.associateBy({ it }, { props.getProperty(it) })
    }


    private fun openFile(): File {
        //val filePath: String = "/${propertiesFile}"
        val outputPath = "${ApplicationData.userPath}${propertiesFile}"
        val outputFile = File(outputPath)
        if (!outputFile.exists()) {
            try {
                javaClass.classLoader.getResourceAsStream(propertiesFile).use {
                    Files.copy(it, outputFile.toPath())
                }
            } catch (e: Exception) {
                ApplicationData.logError(e, "Error copying properties file to : ${outputFile}")
            }
        }
        return File(outputPath)
    }
}
