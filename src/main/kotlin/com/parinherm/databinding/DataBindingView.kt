package com.parinherm.databinding

import com.parinherm.databinding.Converters.updFromDouble
import com.parinherm.databinding.Converters.updToDouble
import org.eclipse.core.databinding.Binding
import org.eclipse.core.databinding.DataBindingContext
import org.eclipse.core.databinding.ValidationStatusProvider
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.value.IObservableValue
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
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import java.math.BigDecimal
import java.time.LocalDate

object DataBindingView{

   private var viewDirty: Boolean = false
   private val listener: IChangeListener = IChangeListener {
      viewDirty = true
   }

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
      val lblHeight = Label(editContainer, SWT.BORDER)
      val txtHeight = Text(editContainer, swnone)
      val lblAge = Label(editContainer, SWT.BORDER)
      val txtAge = Text(editContainer, swnone)
      val lblIncome = Label(editContainer, SWT.BORDER)
      val txtIncome = Text(editContainer, swnone)
      val btnSave = Button(editContainer, SWT.PUSH)
      val wl = WritableList<Map<String, Any>>()
      val wm = WritableMap<String, Any>()
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
         val model = Observables.observeMapEntry(selectedItem as WritableMap<String, Any>,
            "fname" )
         dbc.bindValue(target, model)

         val targetHeight = WidgetProperties.text<Text>(SWT.Modify).observe(txtHeight)
         val modelHeight: IObservableValue<Double> = Observables.observeMapEntry(selectedItem as WritableMap<String, Double>, "height")
         dbc.bindValue<String, Double>(targetHeight, modelHeight, updToDouble, updFromDouble)

         dbc.getBindings().forEach{
            it.target.addChangeListener(listener)
         }

      }
      val firstName = getColumn("First Name", listView, tableLayout)
      listView.contentProvider = ObservableListContentProvider<Map<String, String>>()
      wl.add(makeDomainItem("Wayne", 6.7, 44, BigDecimal(245000)))
      wl.add(makeDomainItem("Belconnen", 4.88, 21, BigDecimal(89000)))
      wl.add(makeDomainItem("Bertrand", 6.1, 32, BigDecimal(22400)))
      listView.input = wl
      lblFirstName.text = "First Name"
      txtFirstName.text = "some text"
      lblHeight.text = "Income"
      lblAge.text = "Age"
      lblIncome.text = "Income"
      btnSave.text = "Save"

      btnSave.addSelectionListener( widgetSelectedAdapter { _ ->
          for (item: Map<String, Any> in wl){
             println("Name: ${item["fname"]} : Height: ${item["height"]} Age: ${item["age"]} Income: ${item["income"]}")
          }
      })
      GridDataFactory.fillDefaults().applyTo(lblFirstName)
      GridDataFactory.fillDefaults().applyTo(lblHeight)
      GridDataFactory.fillDefaults().applyTo(lblAge)
      GridDataFactory.fillDefaults().applyTo(lblIncome)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtFirstName)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtHeight)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtAge)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtIncome)
      composite.layout = FillLayout(SWT.VERTICAL)
      composite.layout()

      return composite
   }

   fun makeDomainItem(firstName: String, height: Double, age: Int, income: BigDecimal) : WritableMap<String, Any> {
      val wm = WritableMap<String, Any>()
      wm["fname"] = firstName
      wm["income"] = income
      wm["height"] = height
      wm["age"] = age
      wm["enteredDate"] = LocalDate.now()
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