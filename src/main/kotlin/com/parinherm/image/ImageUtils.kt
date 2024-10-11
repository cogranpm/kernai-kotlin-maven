package com.parinherm.image

import com.parinherm.ApplicationData
import com.parinherm.entity.LookupDetail
import io.github.classgraph.ClassGraph
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.jface.resource.ImageRegistry
import org.eclipse.swt.graphics.Image
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object ImageUtils {

    private var imageRegistry: ImageRegistry = ImageRegistry()
    private val imageKeys: MutableList<String> = mutableListOf()

    const val IMAGE_LOGO = "napolean-logo-small"
    const val IMAGE_LOGO_SMALL = "napolean-logo-tiny"
    const val IMAGE_LOGO_NORMAL = "napolean-logo"
    const val IMAGE_SYSTEM_LOCK_SCREEN = "system-lock-screen"
    const val IMAGE_APPLICATION_X_EXECUTABLE = "application-x-executable"
    const val IMAGE_APPLICATIONS_INTERNET = "applications-internet"
    const val IMAGE_INTERNET_WEB_BROWSER = "internet-web-browser"
    const val IMAGE_SYSTEM_FILE_MANAGER = "system-file-manager"
    const val IMAGE_MICROPHONE = "audio-input-microphone"
    const val IMAGE_X_OFFICE_DOCUMENT = "x-office-document"
    const val IMAGE_PREFERENCES_SYSTEM = "preferences-system"
    const val IMAGE_FORMAT_JUSTIFY_FILL = "format-justify-fill"
    const val IMAGE_PREFERENCES_DESKTOP = "preferences-desktop"
    const val IMAGE_EDIT_FIND = "edit-find"
    const val IMAGE_APPLICATIONS_SYSTEM = "applications-system"
    const val IMAGE_FACE_GRIN = "face-grin"
    const val IMAGE_SYSTEM_USERS = "system-users"
    const val IMAGE_CONTACT_NEW = "contact-new"
    const val IMAGE_PREFERENCES_DESKTOP_FONT = "preferences-desktop-font"
    const val IMAGE_DIALOG_INFORMATION = "dialog-information"
    const val IMAGE_EDIT_PASTE = "edit-paste"
    const val IMAGE_SYSTEM_INSTALLER = "system-installer"
    const val IMAGE_GENERIC_DOCUMENT = "x-office-document"
    const val IMAGES_PATH = "images"

    fun setupImages() {
        try {
            val userImageFiles = getUserImages()
            userImageFiles.forEach {
                val path = Paths.get(it)
               putUserImage(path.name.substringBeforeLast("."), it)
            }

            val resources = ClassGraph().acceptPaths("${IMAGES_PATH}")
            val scanResult = resources.scan()
            scanResult.allResources.forEach{
                val url = it.url
                val descriptor = ImageDescriptor.createFromURL(url)
                val resourceName = it.pathRelativeToClasspathElement.replace("$IMAGES_PATH/", "")
                val imageKey = resourceName.substringBeforeLast(".")
                this.imageRegistry.put(imageKey, descriptor)
                imageKeys.add(imageKey)
            }
        } catch (e: Exception) {
            ApplicationData.logError(e, "setupImages")
        }
    }


    fun getImages(): List<LookupDetail> {
        val userImageFiles = getUserImages()
        val allImages = userImageFiles as MutableList

        val imageLookups = allImages.map {
            val path = Paths.get(it)
            LookupDetail(0, 0, path.name.toString(), path.name.toString().substringBeforeLast("."))
            //LookupDetail(0, 0, it.fileName.toString(), it.fileName.toString().substringBeforeLast("."))
        }

        val resourceImageLookups = this.imageKeys.map {
            LookupDetail(0, 0, it, it)
        }
        return imageLookups + resourceImageLookups
    }

    fun putUserImage(key: String, filename: String) = try {
        val imagePath = "${ApplicationData.userPath}${File.separator}${IMAGES_PATH}${File.separator}${filename}"
        this.imageRegistry.put(key, ImageDescriptor.createFromFile(null, imagePath))
    } catch (e: Exception) {
        ApplicationData.logError(e, "Error in putUserImage key: ${key} filename: ${filename}")
    }

    fun getImage(name: String): Image? {
        try {
            return this.imageRegistry.get(name)
        } catch (e: Exception){
            ApplicationData.logError(e, "Image Name: $name")
            return null
        }
    }

    fun getImageDescriptor(name: String): ImageDescriptor? {
        try {
            return this.imageRegistry.getDescriptor(name)
        } catch (e: Exception) {
            ApplicationData.logError(e, "Image Name: $name")
            return null
        }
    }

    private fun getUserImages(): List<String> {
        val imagesDir = "${ApplicationData.userPath}${File.separator}${ImageUtils.IMAGES_PATH}${File.separator}"
        val imagesPath = Paths.get(imagesDir)
        val userImages = imagesPath.listDirectoryEntries()
        return userImages.map { it.fileName.toString() }
    }
}