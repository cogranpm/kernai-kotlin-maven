package com.parinherm

import com.parinherm.entity.LookupDetail
import com.parinherm.server.ViewBuilder
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Image

object ApplicationData {

    private val imageRegistry: ImageRegistry = ImageRegistry()
    const val  IMAGE_ACTVITY_SMALL = "activitysmall"
    const val IMAGE_ACTIVITY_LARGE = "activitylarge"
    const val  IMAGE_STOCK_INFO = "stock_info"
    const val IMAGE_STOCK_EXIT = "stock_exit"
    const val  IMAGE_GOUP = "goup"
    const val IMAGES_PATH = "/images/"


    const val swnone = SWT.NONE
    const val labelStyle = SWT.BORDER
    const val listViewStyle = SWT.SINGLE or SWT.H_SCROLL or SWT.V_SCROLL or SWT.FULL_SELECTION or SWT.BORDER

    const val serverPort = "8080"
    const val serverHost = "localhost"
    const val serverProtocol = "http"
    val urls = mapOf<String, String>("views" to "views")

    val views = ViewBuilder.makeDefinitions()

    init {

    }

    fun getView(viewId: String): Map<String, Any> {
        val forms: List<Map<String, Any>> = views[ViewDef.forms] as List<Map<String, Any>>
        return forms.first { it[ViewDef.viewid] == viewId }
    }

    fun makeServerUrl(urlKey: String) : String = "$serverProtocol://$serverHost:$serverPort/${urls[urlKey]}"

    fun setupImages() {
        try {
            putImage(IMAGE_ACTVITY_SMALL, "Activity_16xSM.png")
            putImage(IMAGE_ACTIVITY_LARGE, "Activity_32x.png")
            putImage(IMAGE_GOUP, "go-up.png")
            putImage(IMAGE_STOCK_EXIT, "stock_exit_24.png")
            putImage(IMAGE_STOCK_INFO, "stock_save_24.png")
        }
        catch(e: Exception) {
            println(e)
        }
    }

    private fun putImage(key: String, filename: String) = try {
        val path: String = IMAGES_PATH + filename
        this.imageRegistry.put(key, ImageDescriptor.createFromFile(ApplicationData.javaClass, path))
    } catch ( e: Exception) {
        println(e)
    }

    public fun getImage(name: String): Image {
        return this.imageRegistry.get(name)
    }

    val countryList: List<LookupDetail> = listOf(
        LookupDetail("Aus", "Australia"),
        LookupDetail("Can", "Canada"),
        LookupDetail("Bra", "Brazil"),
        LookupDetail("SA", "South Africa")
    )

    public object ViewDef{
        const val title = "title"
        const val version = "version"
        const val viewid = "viewid"
        const val bindingTestViewId = "bindingtest"
        const val forms = "forms"
        const val fields = "fields"

        //what field from the data (a map) is the input control binding to
        const val fieldName = "fieldName"

        // needed for conversions text to int etc
        //determines what control type is used
        const val fieldDataType = "fieldDataType"
        // possible datatypes
        const val float = "float"
        const val int = "int"
        const val text = "text"
        const val lookup = "lookup"
        const val bool = "bool"
        const val datetime = "datetime"


    }

}