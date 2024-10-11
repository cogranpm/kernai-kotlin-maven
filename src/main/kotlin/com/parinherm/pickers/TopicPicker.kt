package com.parinherm.pickers

import com.parinherm.entity.Topic
import com.parinherm.entity.schema.TopicMapper
import com.parinherm.form.applyLayoutToField
import com.parinherm.form.widgets.BasePicker
import org.eclipse.core.databinding.conversion.IConverter
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.viewers.ArrayContentProvider
import org.eclipse.jface.viewers.ComboViewer
import org.eclipse.jface.viewers.LabelProvider
import org.eclipse.swt.widgets.Composite

object TopicPicker : BasePicker {

    lateinit var dataSource: MutableList<Topic>

    init {
        dataSource = makeDataSource()
    }

    private fun makeDataSource(): MutableList<Topic> {
        //val allTopics = TopicMapper.getAll().toMutableList()
        val blankTopic = Topic.make(0)
        blankTopic.name = "Nothing selected"
        //allTopics.add(0, blankTopic)
        return mutableListOf(blankTopic)
    }

    fun makeDataSource(publicationId: Long) {
        val allTopics = TopicMapper.getAll(mapOf("publicationId" to publicationId)).toMutableList()
        val blankTopic = Topic.make(publicationId)
        blankTopic.name = "Nothing selected"
        allTopics.add(0, blankTopic)
        dataSource = allTopics
    }

    override fun makePickerWidget(parent: Composite, fieldName: String): ComboViewer {
        val input = ComboViewer(parent)
        GridDataFactory.fillDefaults().grab(true, false).applyTo(input.combo)
        input.contentProvider = ArrayContentProvider.getInstance()
        input.labelProvider = (object : LabelProvider() {
            override fun getText(element: Any?): String {
                return if (element != null) {
                    (element as Topic).name
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


    val convertFrom: IConverter<Topic, Long?> = IConverter.create<Topic, Long?> { it.id }

    fun convertTo(list: List<Topic>): IConverter<Long?, Topic> {
        return IConverter.create<Long, Topic> { item: Long ->
            list.find { it.id == item }
        }
    }

    /*
    fun get(publicationId: Long?) : List<Topic>{
        return if(publicationId != null){
            val allTopics = TopicMapper.getAll(mapOf("publicationId" to publicationId)).toMutableList()
            val blankTopic = Topic.make(publicationId)
            blankTopic.name = "Nothing selected"
            allTopics.add(0, blankTopic)
            allTopics
        } else {
            val blankTopic = Topic.make(0)
            blankTopic.name = "Nothing selected"
            listOf(blankTopic)
        }
    }
     */

}