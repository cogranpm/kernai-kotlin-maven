package com.parinherm.databinding

import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.ValidationStatusProvider
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.value.WritableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.ColumnLabelProvider
import org.eclipse.jface.viewers.ColumnWeightData
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.TableViewerColumn
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout.FillLayout

object DataBindingView{

   val swnone = SWT.NONE

   fun makeView(parent: Composite): Composite {
      val composite = Composite(parent, swnone)
      val sashForm = SashForm(composite, swnone)
      val listContainer = Composite(sashForm, swnone)
      val editContainer = Composite(sashForm, swnone)
      val listView = TableViewer(listContainer, swnone)
      val listTable = listView.table
      val tableLayout = TableColumnLayout(true)
      val lblFirstName = Label(editContainer, SWT.BORDER)
      val txtFirstName = Text(editContainer, swnone)
      val btnSave = Button(editContainer, SWT.PUSH)
      val wl = WritableList<Map<String, String>>()
      val wm = WritableMap<String, String>()
      val dbc = DataBindingContext()


      sashForm.weights = intArrayOf(1, 2)
      listContainer.layout = GridLayout(1, true)
      editContainer.layout = GridLayout(2, false)
      listTable.headerVisible = true
      listTable.linesVisible = true
      listContainer.layout = tableLayout
      listView.addSelectionChangedListener { _ ->
         val selection = listView.structuredSelection
         val selectedItem = selection.firstElement
         //setup the databindings
         dbc.dispose()
         val bindings = dbc.validationStatusProviders
         for(binding: ValidationStatusProvider in bindings){
            if(binding is Binding){
               dbc.removeBinding(binding)
            }
         }
         val target = WidgetProperties.text<Text>(SWT.Modify).observe(txtFirstName)
         val model = Observables.observeMapEntry<String, String>(selectedItem as WritableMap<String, String>,
            "fname" )
         dbc.bindValue(target, model)

      }
      val firstName = getColumn("First Name", listView, tableLayout)
      listView.contentProvider = ObservableListContentProvider<Map<String, String>>()
      wm.put("fname", "wayne")
      wl.add(wm)
      wl.add(makeDomainItem("Belconnen"))
      wl.add(makeDomainItem("Bertrand"))
      listView.input = wl
      lblFirstName.text = "First Name"
      txtFirstName.text = "some text"
      btnSave.text = "Save"
      btnSave.addSelectionListener( widgetSelectedAdapter { _ ->
         println(wm.get("fname"))
      })
      GridDataFactory.fillDefaults().applyTo(lblFirstName)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtFirstName)
      composite.layout = FillLayout(SWT.VERTICAL)
      composite.layout()

      return composite
   }

   fun makeDomainItem(firstName: String) : WritableMap<String, String> {
      val wm = WritableMap<String, String>()
      wm.put("fname", firstName)
      return wm
   }

   fun getColumn(caption: String, viewer: TableViewer, layout: TableColumnLayout) : TableViewerColumn {
      val column = TableViewerColumn(viewer, SWT.LEFT)
      val col = column.column
      val colProvider = (object: ColumnLabelProvider() {
         override fun getText(element: Any?): String {
             /* element is a map */
            val map = element as Map<String, String>
            // looks like we'll hard code for now
            return map.getOrDefault("fname", "")
         }

         /*override fun getImage(element: Any?): Image {

         }*/
      })
      col.text = caption
      col.resizable = false
      col.moveable = false
      layout.setColumnData(col, ColumnWeightData(100))
      column.setLabelProvider(colProvider)
      return column
   }
}