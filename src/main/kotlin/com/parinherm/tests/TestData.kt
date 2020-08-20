package com.parinherm.tests

import com.parinherm.ApplicationData.countryList
import org.eclipse.core.databinding.observable.map.WritableMap
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

object TestData {

    val data = listOf<WritableMap<String, Any>>(
        makeDomainItem("Wayne", 6.70, 44, BigDecimal(245000.00), countryList[2].code, false),
        makeDomainItem("Belconnen", 4.88, 21, BigDecimal(89000.00), countryList[1].code, false),
        makeDomainItem("Bertrand", 6.10, 32, BigDecimal(22400.00), countryList[0].code, false)
    )

    private fun makeDomainItem(firstName: String, height: Double, age: Int,
                               income: BigDecimal, country: String, isDeceased: Boolean) : WritableMap<String, Any> {
        val wm = WritableMap<String, Any>()
        wm["fname"] = firstName
        wm["income"] = income
        wm["height"] = height
        wm["age"] = age
        wm["enteredDate"] = LocalDate.now()
        wm["enteredTime"] = LocalTime.of(3, 0, 0)
        wm["country"] = country
        wm["isDeceased"] = isDeceased
        return wm
    }

}