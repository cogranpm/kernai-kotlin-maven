package com.parinherm.tests

import com.parinherm.entity.BeanTest
import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.ModelObject
import java.math.BigDecimal
import java.time.LocalDate

object BeansBindingTestData : IBeanDataEntity {
    override val data: List<BeanTest>
        get() = listOf(make(), make(), make())

    override fun make(): BeanTest{
        return BeanTest("fred", BigDecimal(78543.00), 88.0, 45, LocalDate.now(), "AUS", false)
    }


}