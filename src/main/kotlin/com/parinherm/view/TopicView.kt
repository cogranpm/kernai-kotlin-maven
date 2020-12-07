package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Topic
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class TopicView(val parent: Composite, comparator: BeansViewerComparator)
    : View <Topic> {

        // this member has all of the widgets
        // it's a common object favour composition over inheritance
        override val form: Form<Topic> = Form(parent,
            ApplicationData.getView(ApplicationData.ViewDefConstants.topicViewId),
            comparator
        )

}