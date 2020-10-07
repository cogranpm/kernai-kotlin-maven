package com.parinherm.viewmodel

import com.parinherm.entity.IBeanDataEntity
import com.parinherm.entity.Person
import com.parinherm.entity.schema.BeanTestMapper
import com.parinherm.form.FormViewModel
import com.parinherm.view.PersonView
import com.parinherm.view.View
import org.eclipse.swt.widgets.Composite
import java.math.BigDecimal
import java.time.LocalDate

class PersonViewModel(parent: Composite) : IBeanDataEntity{

    // helper to do all the common stuff relating to viewmodel
    val vm: FormViewModel<PersonViewModel> = FormViewModel(PersonView(parent))

    var entity: Person? = null

    init {
        entity = Person(0, "", BigDecimal("0.0"), 4.5, 22, LocalDate.now(), "Aus", false)
    }

    override var id: Long
        get() = return entity?.id
        set(value) { entity?.id ?:  value}

    override fun getColumnValueByIndex(index: Int): String {
        return if (entity != null) {
            entity!!.getColumnValueByIndex(index)
        } else {
            ""
        }
    }


}