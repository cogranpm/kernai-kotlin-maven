package com.parinherm.builders

import com.parinherm.ApplicationData
import com.parinherm.entity.LookupDetail
import org.eclipse.jface.viewers.ViewerComparator
import org.eclipse.swt.SWT

open class BeansViewerComparator : IViewerComparator, ViewerComparator() {
    var propertyIndex: Int = 0
    val descending: Int = 1
    protected var sort_direction: Int = descending

    init {

    }

    override fun getDirection() : Int {
        return when(sort_direction){
            descending -> SWT.DOWN
            else -> SWT.UP
        }
    }


    override fun setColumn(column: Int) {
        //println("SetColumn column:$column propertyIndex:$propertyIndex")
        if(column == propertyIndex){
            sort_direction = 1 - sort_direction
        } else {
            propertyIndex = column
            sort_direction = descending
        }
    }


    //add implementations for sorting entities by all the types etc
    // especially lookups
    fun compareLookups(e1: String, e2: String, lookupList: List<LookupDetail>): Int {
        val val1 = lookupList.find { it.code == e1}
        val val2 = lookupList.find { it.code == e2}
        if (val1 != null && val2 != null){
            return val1.label.compareTo(val2.label)
        } else {
            return 0
        }
    }

}