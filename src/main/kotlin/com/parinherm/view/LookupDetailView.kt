package com.parinherm.view

import com.parinherm.ApplicationData
import com.parinherm.builders.BeansViewerComparator
import com.parinherm.entity.Lookup
import com.parinherm.entity.LookupDetail
import com.parinherm.form.Form
import org.eclipse.swt.widgets.Composite

class LookupDetailView (val parent: Composite, comparator: BeansViewerComparator) : View<LookupDetail>{

    override val form: Form<LookupDetail> = Form(parent, ApplicationData.getView(ApplicationData.ViewDefConstants.lookupDetailViewId), comparator)
}