package com.parinherm.form.widgets

import com.parinherm.ApplicationData
import com.parinherm.entity.Lookup
import com.parinherm.entity.schema.LookupMapper
import com.parinherm.form.applyLayoutToField
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.swt.widgets.Composite

object LookupPicker: BasePicker {

    lateinit var dataSource: MutableList<Lookup>
    init {
        dataSource = makeDataSource()
    }

    private fun makeDataSource() : MutableList<Lookup> {
        val validItems = LookupMapper.getAll(emptyMap())
        val added = (validItems as MutableList<Lookup>).add(0, Lookup(0, "", "Nothing Selected", false) )
        return validItems
    }


    override fun makePickerWidget(parent: Composite, fieldName: String) : ComboViewer {
        val input = ComboViewer(parent)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
        input.contentProvider = ArrayContentProvider.getInstance()
        input.labelProvider = (object : LabelProvider() {
            override fun getText(element: Any?): String {
                return if(element != null){
                    (element as Lookup).label
                } else {
                    ""
                }
            }
        })
        applyLayoutToField(input.control, true, false)
        input.input = dataSource
        input.setData("fieldName", fieldName)
        return input
    }

}