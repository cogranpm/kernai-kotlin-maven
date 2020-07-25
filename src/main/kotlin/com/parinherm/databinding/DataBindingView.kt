package com.parinherm.databinding

import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.swt.widgets.Label

object DataBindingView{

   fun makeView(parent: Composite): Composite {
      return Composite(parent, SWT.NONE)
   }
}