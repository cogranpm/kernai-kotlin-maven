package com.parinherm

import com.parinherm.entity.LookupDetail
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




    init {

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
        val props = mapOf(
            "version" to "version",
            "inputType" to "controlType",
            "title" to "title",
            "forms" to "forms",
            "fields" to "fields"
        )

    }

}