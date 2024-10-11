package com.parinherm.form.widgets

import com.parinherm.entity.Topic
import com.parinherm.entity.ViewDefinition
import com.parinherm.entity.schema.ViewDefinitionMapper
import com.parinherm.form.applyLayoutToField
import org.eclipse.core.databinding.conversion.IConverter
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.swt.widgets.Composite

object ViewPicker : BasePicker {

    lateinit var dataSource: MutableList<ViewDefinition>
    init {
        dataSource = makeDataSource()
    }

    private fun makeDataSource() : MutableList<ViewDefinition> {
        val validItems = ViewDefinitionMapper.getAll(emptyMap())
        val added = (validItems as MutableList<ViewDefinition>).add(0, ViewDefinition(0, 0, "", "Nothing Selected", 0, 0, "", "", "" ) )
        return validItems
    }


    override fun makePickerWidget(parent: Composite, fieldName: String) : ComboViewer {
        val input = ComboViewer(parent)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
        input.contentProvider = ArrayContentProvider.getInstance()
        input.labelProvider = (object : LabelProvider() {
            override fun getText(element: Any?): String {
                return if(element != null){
                    (element as ViewDefinition).viewId
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

    val convertFrom: IConverter<ViewDefinition, Long?> = IConverter.create<ViewDefinition, Long?> { it.id }

    fun convertTo(list: List<ViewDefinition>): IConverter<Long?, ViewDefinition> {
        return IConverter.create<Long, ViewDefinition> { item: Long ->
            list.find { it.id == item }
        }
    }

}