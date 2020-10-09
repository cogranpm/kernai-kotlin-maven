package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDef

object ViewDefinitions {

    fun makeDefinitions(): Map<String, Any>  {
        // definition of data is something like a view is:
        // a map of properties needed by view
        // a list of maps per view that represents the fields on a form etc etc
        val firstNameDef = mapOf(
            ViewDef.fieldName to "fname",
            ViewDef.title to "First Name",
            ViewDef.fieldDataType to ViewDef.text)

        val incomeDef = mapOf(
            ViewDef.fieldName to "income",
            ViewDef.title to "Income",
            ViewDef.fieldDataType to ViewDef.money)

        val heightDef = mapOf(
                ViewDef.fieldName to "height",
                ViewDef.title to "Height",
                ViewDef.fieldDataType to ViewDef.float)

        val ageDef = mapOf(
                ViewDef.fieldName to "age",
                ViewDef.title to "Age",
                ViewDef.fieldDataType to ViewDef.int)

        val countryDef = mapOf(
                ViewDef.fieldName to "country",
                ViewDef.title to "Country",
                ViewDef.fieldDataType to ViewDef.lookup,
                ViewDef.lookupKey to ApplicationData.countryLookupKey
        )

        val enteredDateDef = mapOf(
                ViewDef.fieldName to "enteredDate",
                ViewDef.title to "Entered",
                ViewDef.fieldDataType to ViewDef.datetime)


        val isDeceasedDef = mapOf(
                ViewDef.fieldName to "isDeceased",
                ViewDef.title to "Deceased",
                ViewDef.fieldDataType to ViewDef.bool)


        val bindingTestDef = mapOf(
            ViewDef.viewid to ViewDef.bindingTestViewId,
            ViewDef.title to "Binding Test",
            ViewDef.fields to listOf(
                    firstNameDef,
                    incomeDef,
                    heightDef,
                    ageDef,
                    countryDef,
                    enteredDateDef,
                    isDeceasedDef
            ))


        val viewDefinitions = mapOf(
            ViewDef.version to 1,
            ViewDef.forms to listOf(bindingTestDef, makeSampleBean(), makePersonDetailDef())
        )

        return viewDefinitions
    }

    fun makePersonDetailDef(): Map<String, Any> {
        /****************** child bean person details ****************/
        val nicknameDef = mapOf(
                ViewDef.fieldName to "nickname",
                ViewDef.title to "Nickname",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.text
        )

        val petSpeciesDef = mapOf(
                ViewDef.fieldName to "petSpecies",
                ViewDef.title to "Pet",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.lookup,
                ViewDef.lookupKey to ApplicationData.speciesLookupKey
        )

        val personDetailsDef = mapOf(
                ViewDef.viewid to ApplicationData.ViewDef.personDetailsViewId,
                ViewDef.title to "Persons Details",
                ViewDef.fields to listOf(nicknameDef, petSpeciesDef)
        )

        return personDetailsDef
    }

    fun makeSampleBean(): Map<String, Any> {
        val firstNameDef = mapOf(
                ViewDef.fieldName to "name",
                ViewDef.title to "First Name",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.text)

        val incomeDef = mapOf(
                ViewDef.fieldName to "income",
                ViewDef.title to "Income",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.money)

        val heightDef = mapOf(
                ViewDef.fieldName to "height",
                ViewDef.title to "Height",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.float)

        val ageDef = mapOf(
                ViewDef.fieldName to "age",
                ViewDef.title to "Age",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.int)

        val countryDef = mapOf(
                ViewDef.fieldName to "country",
                ViewDef.title to "Country",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.lookup,
                ViewDef.lookupKey to ApplicationData.countryLookupKey
        )

        val enteredDateDef = mapOf(
                ViewDef.fieldName to "enteredDate",
                ViewDef.title to "Entered",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.datetime
        )


        val isDeceasedDef = mapOf(
                ViewDef.fieldName to "deceased",
                ViewDef.title to "Deceased",
                ViewDef.required to true,
                ViewDef.fieldDataType to ViewDef.bool)


        val viewDef = mapOf(
                ViewDef.viewid to ViewDef.personViewId,
                ViewDef.title to "Beans Binding Test",
                ViewDef.fields to listOf(
                        firstNameDef,
                        incomeDef,
                        heightDef,
                        ageDef,
                        countryDef,
                        enteredDateDef,
                        isDeceasedDef
                ),
                ViewDef.childViews to listOf(makePersonDetailDef())
        )
        return viewDef

    }
}