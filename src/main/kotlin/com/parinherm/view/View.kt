package com.parinherm.view

import com.parinherm.entity.IBeanDataEntity
import com.parinherm.form.Form

interface View <T> where T: IBeanDataEntity{
    val form: Form<T>
}