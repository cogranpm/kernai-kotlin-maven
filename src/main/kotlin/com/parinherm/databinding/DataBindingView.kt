package com.parinherm.databinding

import com.parinherm.ApplicationData.countryList
import com.parinherm.databinding.Converters.convertFromLookup
import com.parinherm.databinding.Converters.convertToLookup
import com.parinherm.databinding.Converters.updFromBigDecimal
import com.parinherm.databinding.Converters.updFromDouble
import com.parinherm.databinding.Converters.updFromInt
import com.parinherm.databinding.Converters.updToBigDecimal
import com.parinherm.databinding.Converters.updToDouble
import com.parinherm.databinding.Converters.updToInt
import com.parinherm.entity.LookupDetail
import jdk.dynalink.linker.support.Lookup
import org.eclipse.core.databinding.*
import org.eclipse.core.databinding.observable.IChangeListener
import org.eclipse.core.databinding.observable.Observables
import org.eclipse.core.databinding.observable.list.WritableList
import org.eclipse.core.databinding.observable.map.WritableMap
import org.eclipse.core.databinding.observable.value.IObservableValue
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport
import org.eclipse.jface.databinding.swt.ISWTObservableValue
import org.eclipse.jface.databinding.swt.typed.WidgetProperties
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider
import org.eclipse.jface.layout.GridDataFactory
import org.eclipse.jface.layout.TableColumnLayout
import org.eclipse.jface.viewers.*
import org.eclipse.swt.SWT
import org.eclipse.swt.custom.SashForm
import org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

object DataBindingView{

   private var viewDirty: Boolean = false
   private val listener: IChangeListener = IChangeListener {
      viewDirty = true
   }

   val swnone = SWT.NONE
   val labelStyle = SWT.BORDER
   val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER


   fun makeView(parent: Composite): Composite {
      val composite = Composite(parent, swnone)
      val sashForm = SashForm(composite, SWT.BORDER)
      val listContainer = Composite(sashForm, swnone)
      val editContainer = Composite(sashForm, swnone)
      val listView = TableViewer(listContainer, listViewStyle)
      val listTable = listView.table
      val tableLayout = TableColumnLayout(true)
      val lblFirstName = Label(editContainer, labelStyle)
      val txtFirstName = Text(editContainer, swnone)
      val lblCountry = Label(editContainer, labelStyle)
      val cboCountry = ComboViewer(editContainer)
      val lblHeight = Label(editContainer, labelStyle)
      val txtHeight = Text(editContainer, swnone)
      val lblAge = Label(editContainer, labelStyle)
      val txtAge = Text(editContainer, swnone)
      val lblIncome = Label(editContainer, labelStyle)
      val txtIncome = Text(editContainer, swnone)
      val lblEnteredDate = Label(editContainer, labelStyle)
      val wEnteredDate = DateTime(editContainer, SWT.DROP_DOWN or SWT.DATE)
      val lblEnteredTime = Label(editContainer, labelStyle)
      val wEnteredTime = DateTime(editContainer, SWT.DROP_DOWN or SWT.TIME)
      val lblErrors = Label(editContainer, labelStyle)
      val btnSave = Button(editContainer, SWT.PUSH)
      val wl = WritableList<Map<String, Any>>()
      val dbc = DataBindingContext()


      sashForm.weights = intArrayOf(1, 2)
      sashForm.sashWidth = 4
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
         val targetFirstName = WidgetProperties.text<Text>(SWT.Modify).observe(txtFirstName)
         val modelFirstName = Observables.observeMapEntry(selectedItem as WritableMap<String, String>,"fname" )
         val bindFirstName = dbc.bindValue(targetFirstName, modelFirstName)
         ControlDecorationSupport.create(bindFirstName, SWT.TOP or SWT.LEFT)

         val targetCountry = WidgetProperties.comboSelection().observe(cboCountry.combo)
         val modelCountry = Observables.observeMapEntry(selectedItem as WritableMap<String, Any>, "country")
         val bindCountry = dbc.bindValue(targetCountry, modelCountry) //, UpdateValueStrategy.create(convertFromLookup), UpdateValueStrategy.create(convertToLookup(countryList)))

         val targetHeight = WidgetProperties.text<Text>(SWT.Modify).observe(txtHeight)
         val modelHeight: IObservableValue<Double> = Observables.observeMapEntry(selectedItem as WritableMap<String, Double>, "height")
         val bindHeight = dbc.bindValue<String, Double>(targetHeight, modelHeight, updToDouble, updFromDouble)
         ControlDecorationSupport.create(bindHeight, SWT.TOP or SWT.LEFT)

         val targetAge = WidgetProperties.text<Text>(SWT.Modify).observe(txtAge)
         val modelAge: IObservableValue<Int> = Observables.observeMapEntry(selectedItem as WritableMap<String, Int>, "age")
         val bindAge = dbc.bindValue<String, Int>(targetAge, modelAge, updToInt, updFromInt)
         ControlDecorationSupport.create(bindAge, SWT.TOP or SWT.LEFT)

         val targetIncome = WidgetProperties.text<Text>(SWT.Modify).observe(txtIncome)
         val modelIncome: IObservableValue<BigDecimal> = Observables.observeMapEntry(selectedItem as WritableMap<String, BigDecimal>, "income")
         val bindIncome = dbc.bindValue(targetIncome, modelIncome, updToBigDecimal, updFromBigDecimal)
         ControlDecorationSupport.create(bindIncome, SWT.TOP or SWT.LEFT)

         val enteredDateSelectionProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
         val targetEnteredDate = enteredDateSelectionProperty.observe(wEnteredDate)
         val modelEnteredDate = Observables.observeMapEntry(selectedItem as WritableMap<String, LocalDate>, "enteredDate")
         val bindEnteredDate = dbc.bindValue(targetEnteredDate, modelEnteredDate)
         ControlDecorationSupport.create(bindEnteredDate, SWT.TOP or SWT.LEFT)

         // this one isn't working form some reason
         val enteredTimeSelectionProperty: DateTimeSelectionProperty = DateTimeSelectionProperty()
         val targetEnteredTime = enteredTimeSelectionProperty.observe(wEnteredTime)
         val modelEnteredTime = Observables.observeMapEntry(selectedItem as WritableMap<String, LocalTime>, "enteredTime")
         val bindEnteredTime = dbc.bindValue(targetEnteredTime, modelEnteredTime)
         ControlDecorationSupport.create(bindEnteredTime, SWT.TOP or SWT.LEFT)

         dbc.bindings.forEach{
            it.target.addChangeListener(listener)
         }

         val  errorObservable: IObservableValue<String> = WidgetProperties.text<Label>().observe(lblErrors)
         val allValidationBinding: Binding = dbc.bindValue(errorObservable,
            AggregateValidationStatus(dbc.bindings, AggregateValidationStatus.MAX_SEVERITY), null, null)

      }
      val firstName = getColumn("First Name", listView, tableLayout)
      listView.contentProvider = ObservableListContentProvider<Map<String, String>>()
      wl.add(makeDomainItem("Wayne", 6.70, 44,
         BigDecimal(245000.00), countryList[2].code))
      wl.add(makeDomainItem("Belconnen", 4.88, 21,
         BigDecimal(89000.00), countryList[1].code))
      wl.add(makeDomainItem("Bertrand", 6.10, 32,
         BigDecimal(22400.00), countryList[0].code)
      )
      listView.input = wl
      lblFirstName.text = "First Name"
      txtFirstName.text = "some text"
      lblCountry.text = "Country"
      lblHeight.text = "Height"
      lblAge.text = "Age"
      lblIncome.text = "Income"
      lblEnteredDate.text = "Date Entered"
      lblEnteredTime.text = "Time Entered"
      btnSave.text = "Save"

      cboCountry.contentProvider = ArrayContentProvider.getInstance()
      cboCountry.labelProvider = (object: LabelProvider() {
         override fun getText(element: Any) : String {
            return (element as LookupDetail).label
         }
      })
      cboCountry.input = countryList

      btnSave.addSelectionListener( widgetSelectedAdapter { _ ->
          for (item: Map<String, Any> in wl){
             println("Name: ${item["fname"]}: " +
                     "Country: ${item["country"]} " +
                     "Height: ${item["height"]} Age: ${item["age"]} " +
                     "Income: ${item["income"]} Entered: ${item["enteredDate"]} " +
                     "Time: ${item["enteredTime"]}")
          }
      })
      GridDataFactory.fillDefaults().applyTo(lblFirstName)
      GridDataFactory.fillDefaults().applyTo(lblCountry)
      GridDataFactory.fillDefaults().applyTo(lblHeight)
      GridDataFactory.fillDefaults().applyTo(lblAge)
      GridDataFactory.fillDefaults().applyTo(lblIncome)
      GridDataFactory.fillDefaults().applyTo(lblEnteredDate)
      GridDataFactory.fillDefaults().applyTo(lblEnteredTime)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtFirstName)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(cboCountry.control)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtHeight)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtAge)
      GridDataFactory.fillDefaults().grab(true, false).applyTo(txtIncome)
      GridDataFactory.fillDefaults().applyTo(wEnteredDate)
      GridDataFactory.fillDefaults().applyTo(wEnteredTime)
      GridDataFactory.fillDefaults().span(2, 1).applyTo(lblErrors)
      composite.layout = FillLayout(SWT.VERTICAL)
      composite.layout()

      return composite
   }

   fun makeDomainItem(firstName: String, height: Double, age: Int, income: BigDecimal, country: String) : WritableMap<String, Any> {
      val wm = WritableMap<String, Any>()
      wm["fname"] = firstName
      wm["income"] = income
      wm["height"] = height
      wm["age"] = age
      wm["enteredDate"] = LocalDate.now()
      wm["enteredTime"] = LocalTime.of(3, 0, 0)
      wm["country"] = country
      return wm
   }

   fun getColumn(caption: String, viewer: TableViewer, layout: TableColumnLayout) : TableViewerColumn {
      val column = TableViewerColumn(viewer, SWT.LEFT)
      val col = column.column
      val colProvider = (object: ColumnLabelProvider() {
         override fun getText(element: Any?): String {
             /* element is a map */
            val map = element as Map<String, Any>
            // looks like we'll hard code for now
            return map.getOrDefault("fname", "").toString()
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