package com.parinherm.entity.schema

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.dbTypeEmbedded
import com.parinherm.ApplicationData.dbTypeMySql
import com.parinherm.ApplicationData.dbTypePostgres
import com.parinherm.ApplicationData.dbTypeSqlite
import com.parinherm.entity.QuizRun
import com.parinherm.settings.Setting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
//import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
//import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.util.*

object SchemaBuilder {


    fun connect(setting: Setting): Database? {
        val database: Database? = when (setting.dbType) {
            dbTypeMySql -> {
                return Database.connect(
                    setting?.dbUrl ?: "",
                    driver = setting?.dbDriver ?: "",
                    user = setting?.dbUser ?: "",
                    password = setting?.dbPassword ?: ""
                )
            }
            dbTypePostgres -> {
                val tempDB = Database.connect(
                    setting?.dbUrl ?: "",
                    driver = setting?.dbDriver ?: "",
                    user = setting?.dbUser ?: "",
                    password = setting?.dbPassword ?: ""
                )
                transaction {
                    val schema = Schema("public")
                    SchemaUtils.setSchema(schema)
                }
                return tempDB
            }
            dbTypeEmbedded -> {
                //return Database.connect("jdbc:sqlite::resource:kernai.db")
                val embeddedPath = "${ApplicationData.userPath}${ApplicationData.DATA_PATH}${File.separator}${ApplicationData.APPLICATION_NAME}.db"
                return Database.connect("jdbc:sqlite:${embeddedPath}")
            }
            dbTypeSqlite -> {
                return Database.connect(setting?.dbUrl ?: "")
            }
            else -> {
                return null
            }
        }
        return database
    }

    var db: Database? = null

    fun build() {
        if (db == null) {
            return
        }
        transaction {
            //addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                Answers,
                AssociationDefinitions,
                Ingredients,
                Lookups,
                LookupDetails,
                Logins,
                Notebooks,
                NoteDetails,
                NoteHeaders,
                Notes,
                NoteSegments,
                NoteSegmentTypeHeaders,
                NoteSegmentTypes,
                PersonDetails,
                Persons,
                Publications,
                Questions,
                Quizs,
                Recipes,
                Shelfs,
                Snippets,
                Subjects,
                Topics,
                ViewDefinitions,
                FieldDefinitions,
                Menus,
                MenuItems,
                AppVersions,
                QuizRuns,
                QuizRunQuestions,
                SalesforceConfigs
            )
        }
    }

    fun updateSchema(){
        /*
       transaction {
           SchemaUtils.createMissingTablesAndColumns(SalesforceConfigs)
       }
       */

    }

    fun buildSchema(tables: List<LongIdTable>) {
        if (db == null) {
            return
        }
        transaction {
            addLogger(StdOutSqlLogger)
            //SchemaUtils.create(table)
            SchemaUtils.create(*tables.toTypedArray())
            //SchemaUtils.createMissingTablesAndColumns(Answers, Questions)
        }
    }

    fun close(){
        if(db != null){
            TransactionManager.closeAndUnregister(db!!)
        }
    }
}

/*
val db = Database.connect(
    "jdbc:postgresql://localhost/golangtest",
    driver = "org.postgresql.Driver",
    user = "postgres",
    password = "REDDINGO"
)
 */


/*
 if keeping database in the jar as a resource
this doesn't work real good because it's copied when maven does the packaging
thus can't be shared via source control

val db = Database.connect(
    "jdbc:sqlite::resource:kernai.db.backup"
)

val db = Database.connect(
    "jdbc:sqlite:\\\\media-server\\mediafiles\\paul\\projects\\database\\kernai-kotlin-maven\\kernai.db.backup"
)

*/


//val uri = System.getenv("KERNAIDB_URI")
//val uri = ":resource:kernai.db.backup"
/*
val db = Database.connect(
    "jdbc:sqlite:$uri"
)
 */


