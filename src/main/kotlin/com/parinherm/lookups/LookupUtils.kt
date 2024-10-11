package com.parinherm.lookups

import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.entity.schema.LookupDetailMapper
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.font.FontUtils
import com.parinherm.form.definitions.ViewDefConstants
import com.parinherm.image.ImageUtils
import com.parinherm.script.ScriptUtils

object LookupUtils {

    init {

    }

    //lateinit var lookups: MutableMap<String, List<LookupDetail>>
    private val lazyLookups: MutableMap<String, List<LookupDetail>> = mutableMapOf()
    const val lookupFieldLength: Int = 20

    /* convenience constants for lookups used in built in forms */
    const val countryLookupKey = "country"
    const val speciesLookupKey = "species"
    const val recipeCategoryLookupKey = "recipecat"
    const val unitLookupKey = "unit"
    const val techLanguageLookupKey = "techlang"
    const val snippetCategoryKey = "cat"
    const val snippetTopicKey = "topic"
    const val snippetTypeKey = "type"
    const val passwordMasterKey = "password_master"
    const val loginCategoryKey = "logcat"
    const val publicationTypeLookupKey = "pubtype"
    const val sashOrientationLookupKey = "sash_orientation"
    const val fieldSizeLookupKey = "field_size"
    const val dataTypeLookupKey = "data_type"
    const val modifierKeyLookupKey = "modifier_key"
    const val fontLookupKey = "font"
    const val imageLookupKey = "image"
    const val associationTypeLookupKey = "associationType"

    /*
    val countryList: List<LookupDetail> by lazy { lookups.getOrDefault(countryLookupKey, emptyList()) }
    val speciesList: List<LookupDetail> by lazy { lookups.getOrDefault(speciesLookupKey, emptyList()) }
    val recipeCategoryList by lazy { lookups.getOrDefault(recipeCategoryLookupKey, emptyList()) }
    val unitList: List<LookupDetail> by lazy { lookups.getOrDefault(unitLookupKey, emptyList()) }
    val techLanguage: List<LookupDetail> by lazy { lookups.getOrDefault(techLanguageLookupKey, emptyList()) }
    val snippetCategory: List<LookupDetail> by lazy { lookups.getOrDefault(snippetCategoryKey, emptyList()) }
    val snippetTopic: List<LookupDetail> by lazy { lookups.getOrDefault(snippetTopicKey, emptyList()) }
    val snippetType: List<LookupDetail> by lazy { lookups.getOrDefault(snippetTypeKey, emptyList()) }
    val passwordMaster: List<LookupDetail> by lazy { lookups.getOrDefault(passwordMasterKey, emptyList()) }
    val loginCategoryList by lazy { lookups.getOrDefault(loginCategoryKey, emptyList()) }
    val publicationTypeList by lazy { lookups.getOrDefault(publicationTypeLookupKey, emptyList()) }
    val sashOrientationList by lazy { lookups.getOrDefault(sashOrientationLookupKey, emptyList()) }
    val fieldSizeList by lazy { lookups.getOrDefault(fieldSizeLookupKey, emptyList())}
    val dataTypeLookupList by lazy { lookups.getOrDefault(dataTypeLookupKey, emptyList())}
     */

    fun load(){

        /* for performance reasons this should be made lazy
        *  each key in the map should be a lambda that loads the lookup details
        * */
        //lookups = LookupMapper.getLookup(null)
        lazyLookups.clear()
        val allKeys = LookupMapper.getAllKeys()
        allKeys.forEach{
            val x by lazy {  LookupDetailMapper.getByLookupKey(mapOf("key" to it))}
            lazyLookups[it] = x
        }
    }

   fun reloadLookup(lookupId: Long) {
        /* when user modifies a lookup record, refreshed the cached version of the lookup */
        val lookup = LookupMapper.getLookup(lookupId)
        lookup.forEach {
            lazyLookups[it.key] = it.value
        }
    }

    fun makeNullLookupDetail(): LookupDetail =
        LookupDetail(0, 0, "", "make selection")

    fun getLookupByKey(key: String, addNullItem: Boolean): List<LookupDetail>{
        //val list = lookups[key]?: listOf()
        val list = lazyLookups[key]?: listOf()
        if(addNullItem){
            val nullItem = makeNullLookupDetail()
            val nullItemExists = list.elementAt(0).id == 0L
            return if(!nullItemExists){
                //val mutableList = list.toMutableList()
                val mutableList = list as MutableList
                mutableList.add(0, nullItem)
                //mutableList.toList()
                mutableList as List<LookupDetail>
            } else {
                list
            }
        }
        return list
    }

    fun getLookupIdByKey(key: String): Long {
        val lookups = LookupMapper.getByKey(key)
        return lookups[0].id
    }

    fun createDefaults() {
        /*
        these lookup records must exist or errors will result
        */

        val loginCategory = Lookup(0, loginCategoryKey, "Login Category", false)
        val loginCategoryDetails = LookupDetail(0, 0, "default", "Default")
        createDefault(loginCategory, listOf(loginCategoryDetails))

        val passwordMaster = Lookup(0, passwordMasterKey, "Password Master", true)
        createDefault(passwordMaster, listOf())

        val pubType = Lookup(0, publicationTypeLookupKey, "Publication Type", false)
        val pubTypeDefaults = listOf(LookupDetail(0, 0, "book", "Book"))
        createDefault(pubType, pubTypeDefaults)

        val recipeCat = Lookup(0, recipeCategoryLookupKey, "Recipe Category", false)
        val recipeCatDefaults = listOf(LookupDetail(0, 0, "main", "Main"))
        createDefault(recipeCat, recipeCatDefaults)

        val unit = Lookup(0, unitLookupKey, "Unit", false)
        val unitDefaults = listOf(
            LookupDetail(0, 0, "cup", "Cup"),
            LookupDetail(0, 0, "can", "Can"),
            LookupDetail(0, 0, "dp", "Drop"),
            LookupDetail(0, 0, "hnt", "Hint"),
            LookupDetail(0, 0, "jar", "Jar"),
            LookupDetail(0, 0, "pch", "Pinch"),
            LookupDetail(0, 0, "pkt", "Packet"),
            LookupDetail(0, 0, "pnd", "Pound"),
            LookupDetail(0, 0, "prt", "Part"),
            LookupDetail(0, 0, "tbl", "Tablespoon"),
            LookupDetail(0, 0, "whl", "Whole"),
            LookupDetail(0, 0, "tsp", "Teaspoon")
        )
        createDefault(unit, unitDefaults)

        val species = Lookup(0, speciesLookupKey, "Species", false)
        val speciesDefaults = listOf(LookupDetail(0, 0, "HUM", "Human"))
        createDefault(species, speciesDefaults)

        val country = Lookup(0, countryLookupKey, "Country", false)
        val countryDefaults = listOf(LookupDetail(0, 0, "AUS", "Australia"))
        createDefault(country, countryDefaults)

        val sc = Lookup(0, snippetCategoryKey, "Snippet Category", false)
        val scDefaults = listOf(LookupDetail(0, 0, "def", "Default"))
        createDefault(sc, scDefaults)

        val tl = Lookup(0, techLanguageLookupKey, "Snippet Language", false)
        val tlDefaults = listOf(
            LookupDetail(0, 0, ScriptUtils.kotlinCode, "Kotlin"),
            LookupDetail(0, 0, ScriptUtils.pythonCode, "Python"),
            LookupDetail(0, 0, ScriptUtils.rCode, "R"),
            LookupDetail(0, 0, ScriptUtils.rubyCode, "Ruby"),
            LookupDetail(0, 0, ScriptUtils.javascriptCode, "Javascript"),
            LookupDetail(0, 0, ScriptUtils.cSharpCode, "C#")
        )
        createDefault(tl, tlDefaults)

        val st = Lookup(0, snippetTopicKey, "Snippet Topic", false)
        val stDefaults = listOf(LookupDetail(0, 0, "def", "Default"))
        createDefault(st, stDefaults)

        val sType = Lookup(0, snippetTypeKey, "Snippet Type", false)
        val sTypeDefaults = listOf(LookupDetail(0, 0, "def", "Default"))
        createDefault(sType, sTypeDefaults)

        val sashOrientation = Lookup(0, sashOrientationLookupKey, "Sash Orientation", false)
        val sashOrientationDefaults = listOf(
            LookupDetail(0, 0, ViewDefConstants.horizontal, "Horizontal"),
            LookupDetail(0, 0, ViewDefConstants.vertical, "Vertical")
        )
        createDefault(sashOrientation, sashOrientationDefaults)

        val fieldSize = Lookup(0, fieldSizeLookupKey, "Field Size", false)
        val fieldSizeDefaults = listOf(
            LookupDetail(0, 0, "LARGE", "Large"),
            LookupDetail(0, 0, "MEDIUM", "Medium"),
            LookupDetail(0, 0, "SMALL", "Small")
        )
        createDefault(fieldSize, fieldSizeDefaults)

        val dataType = Lookup(0, dataTypeLookupKey, "Data Type", false)
        val dataTypeDefaults = listOf(
            LookupDetail(0, 0, "TEXT", "Text"),
            LookupDetail(0, 0, "MEMO", "Memo"),
            LookupDetail(0, 0, "FLOAT", "Float"),
            LookupDetail(0, 0, "INT", "Int"),
            LookupDetail(0, 0, "LOOKUP", "Lookup"),
            LookupDetail(0, 0, "BOOLEAN", "Boolean"),
            LookupDetail(0, 0, "DATETIME", "Date Time"),
            LookupDetail(0, 0, "DATE", "Date"),
            LookupDetail(0, 0, "TIME", "Time"),
            LookupDetail(0, 0, "MONEY", "Money"),
            LookupDetail(0, 0, "SOURCE", "Source"),
            LookupDetail(0, 0, "REFERENCE", "Reference"),
            LookupDetail(0, 0, "FILE", "File")
        )
        createDefault(dataType, dataTypeDefaults)

        val modifierKey = Lookup(0, modifierKeyLookupKey, "Modifier Key", false)
        val modifierKeyDefaults = listOf(
            LookupDetail(0, 0, "ALT", "Alt"),
            LookupDetail(0, 0, "COMMAND", "Command"),
            LookupDetail(0, 0, "CONTROL", "Control"),
            LookupDetail(0, 0, "SHIFT", "Shift"),
            LookupDetail(0, 0, "MOD1", "Mod 1"),
            LookupDetail(0, 0, "MOD2", "Mod 2"),
            LookupDetail(0, 0, "MOD3", "Mod 3"),
            LookupDetail(0, 0, "MOD4", "Mod 4")
        )
        createDefault(modifierKey, modifierKeyDefaults)

        val font = Lookup(0, fontLookupKey, "Fonts", false)
        val fontDefaults = listOf(
            LookupDetail(0, 0, FontUtils.FONT_IBM_PLEX_MONO, "IBM Plex Mono ExtraLight")
        )
        createDefault(font, fontDefaults)

        val imageLookup = Lookup(0, imageLookupKey, "Images", false)
        val defaultImages = ImageUtils.getImages()
        createDefault(imageLookup, defaultImages)

        val associationTypeLookup = Lookup(0, associationTypeLookupKey, "Association Type", false)
        val associationTypeDefaults = listOf(
            LookupDetail(0, 0, "none", "None"),
            LookupDetail(0, 0, "one", "One"),
            LookupDetail(0, 0, "many", "Many")
        )
        createDefault(associationTypeLookup, associationTypeDefaults)

    }

    private fun createDefault(lookup: Lookup, lookupDetails: List<LookupDetail>?) {
        val existing = LookupMapper.getByKey(lookup.key)
        if (existing.isEmpty()) {
            LookupMapper.save(lookup)
            if (lookupDetails != null) {
                lookupDetails.forEach {
                    it.lookupId = lookup.id
                    LookupDetailMapper.save(it)
                }
            }
        } else {
            val existingLookup = existing[0]
            if (lookupDetails != null) {
                lookupDetails.forEach {
                    it.lookupId = existingLookup.id
                    if(!LookupDetailMapper.exists(existingLookup.id, it.code)){
                        LookupDetailMapper.save(it)
                    }
                }
            }
        }
    }


}