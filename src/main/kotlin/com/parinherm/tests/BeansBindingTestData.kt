package com.parinherm.tests

import com.parinherm.entity.BeanTest
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.ModelObject
import java.math.BigDecimal
import java.time.LocalDate

object BeansBindingTestData : IBeanDataEntity {
    override val data: List<BeanTest>
        get() = listOf(
                BeanTest("Wayne", BigDecimal(78543.00), 5.9, 43, LocalDate.now(), "Aus", true),
                BeanTest("Belconnon", BigDecimal(44400.00), 4.9, 55, LocalDate.now(), "Can", false),
                BeanTest("Baumauris", BigDecimal(890.00), 6.4, 25, LocalDate.now(), "SA", true)
        )

    override fun make(): BeanTest{
        return BeanTest("fred", BigDecimal(78543.00), 88.0, 45, LocalDate.now(), "Aus", true)
    }


}