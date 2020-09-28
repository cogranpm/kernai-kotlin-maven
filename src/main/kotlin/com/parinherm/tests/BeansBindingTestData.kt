package com.parinherm.tests

import com.parinherm.entity.BeanTest
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.ModelObject
import com.parinherm.entity.PersonDetail
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate

object BeansBindingTestData {
    val data: List<BeanTest>
        get() = listOf(
                BeanTest(1, "Wayne", BigDecimal(78543.00), 5.9, 43, LocalDate.now(), "Aus", true),
                BeanTest(2, "Belconnon", BigDecimal(44400.00), 4.9, 55, LocalDate.parse("1978-09-15"), "Can", false),
                BeanTest(3, "Baumauris", BigDecimal(890.00), 6.4, 25, LocalDate.parse("2012-03-15"), "SA", true)
        )

    fun make(): BeanTest{
        return BeanTest(0, "", BigDecimal(0), 0.0, 0, LocalDate.now(), "Aus", false)
    }

    val personDetails = listOf<PersonDetail>(PersonDetail(1, "Ralfie", 1), PersonDetail(2, "Beans", 1))


}