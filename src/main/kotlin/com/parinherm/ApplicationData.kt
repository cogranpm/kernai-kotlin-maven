package com.parinherm

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.graphics.Image

object ApplicationData {

    private var imageRegistry: ImageRegistry = ImageRegistry()
    const val  IMAGE_ACTVITY_SMALL = "activitysmall"
    const val IMAGE_ACTIVITY_LARGE = "activitylarge"
    const val  IMAGE_STOCK_INFO = "stock_info"
    const val IMAGE_STOCK_EXIT = "stock_exit"
    const val  IMAGE_GOUP = "goup"
    const val IMAGES_PATH = "/images/"


    init {

    }



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

}