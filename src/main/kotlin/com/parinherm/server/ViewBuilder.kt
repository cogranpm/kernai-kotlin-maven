package com.parinherm.server

import com.parinherm.ApplicationData
import com.parinherm.ApplicationData.ViewDef
import com.parinherm.entity.BeanTest

object ViewBuilder {

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
            ViewDef.forms to listOf(bindingTestDef, makeSampleBean())
        )

        return viewDefinitions
    }

    fun makeSampleBean(): Map<String, Any> {
        val firstNameDef = mapOf(
                ViewDef.fieldName to "fname",
                ViewDef.title to "First Name",
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).name}"},
                ViewDef.fieldDataType to ViewDef.text)

        val incomeDef = mapOf(
                ViewDef.fieldName to "income",
                ViewDef.title to "Income",
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).income}"},
                ViewDef.fieldDataType to ViewDef.money)

        val heightDef = mapOf(
                ViewDef.fieldName to "height",
                ViewDef.title to "Height",
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).height}"},
                ViewDef.fieldDataType to ViewDef.float)

        val ageDef = mapOf(
                ViewDef.fieldName to "age",
                ViewDef.title to "Age",
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).age}"},
                ViewDef.fieldDataType to ViewDef.int)

        val countryDef = mapOf(
                ViewDef.fieldName to "country",
                ViewDef.title to "Country",
                ViewDef.fieldDataType to ViewDef.lookup,
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).country}"},
                ViewDef.lookupKey to ApplicationData.countryLookupKey
        )

        val enteredDateDef = mapOf(
                ViewDef.fieldName to "enteredDate",
                ViewDef.title to "Entered",
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).enteredDate}"},
                ViewDef.fieldDataType to ViewDef.datetime
        )


        val isDeceasedDef = mapOf(
                ViewDef.fieldName to "isDeceased",
                ViewDef.title to "Deceased",
                ViewDef.fieldDataType to ViewDef.bool,
                ViewDef.fieldLabelConverter to {element: Any? -> "${(element as BeanTest).deceased}"})


        val viewDef = mapOf(
                ViewDef.viewid to ViewDef.beansBindingTestViewId,
                ViewDef.title to "Beans Binding Test",
                ViewDef.fields to listOf(
                        firstNameDef,
                        incomeDef,
                        heightDef,
                        ageDef,
                        countryDef,
                        enteredDateDef,
                        isDeceasedDef
                ))
        return viewDef

    }
}