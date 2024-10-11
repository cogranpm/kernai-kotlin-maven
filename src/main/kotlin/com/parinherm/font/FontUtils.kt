package com.parinherm.font

import com.parinherm.ApplicationData
import org.eclipse.jface.resource.FontRegistry
import org.eclipse.swt.SWT
import org.eclipse.swt.graphics.Font
import org.eclipse.swt.graphics.FontData
import org.eclipse.swt.widgets.Display
import java.io.File
import java.nio.file.Files

object FontUtils {

    private var fontRegistry: FontRegistry

    init {
        fontRegistry = FontRegistry()
    }

    const val FONT_PATH = "fonts"
    const val FONT_IBM_PLEX_MONO = "ibmplexmono"
    const val FONT_IBM_PLEX_MONO_HEADER = "ibmplexmonoheader"
    const val FONT_IBM_PLEX_MONO_BIG = "ibmplexmonobig"

    fun setupFonts() {
        putFont(FONT_IBM_PLEX_MONO, "IBMPlexMono-ExtraLight.ttf", "IBM Plex Mono ExtraLight", 10, SWT.NORMAL)
        putFont(FONT_IBM_PLEX_MONO_BIG, "IBMPlexMono-BoldItalic.ttf", "IBM Plex Mono BoldItalic", 26, SWT.BOLD)
        //note: can't load same font for different size/style, need to rethink, maybe a list of pairs
        //putFont(FONT_IBM_PLEX_MONO_HEADER, "IBMPlexMono-ExtraLight.ttf", "IBM Plex Mono ExtraLight", 18, SWT.BOLD)
    }

    fun putFont(key: String, fileName: String, name: String, size: Int, style: Int) {
        try {
            val fontPath: String = "/${FONT_PATH}/${fileName}"
            val outputFile = File("${ApplicationData.userPath}${File.separator}${fileName}")
            if (!outputFile.exists()) {
                try {
                    val fontStream = this.javaClass.getResourceAsStream(fontPath).use {
                        Files.copy(it, outputFile.toPath())
                    };
                } catch (e: Exception) {
                    ApplicationData.logError(e, "Error copying font $fileName Message: ${e.message}")
                }
            }
            val didLoad = Display.getCurrent().loadFont(outputFile.path);
            if (didLoad) {
                val fontData = FontData(name, size, style)
                this.fontRegistry.put(key, arrayOf(fontData))
            } else {
                ApplicationData.logError(Exception("Error loading font."), "Font $fileName could not be loaded")
            }
        } catch (e: Exception) {
            ApplicationData.logError(e, "Font $fileName could not be registered")
        }

    }

    fun getFont(name: String): Font {
        return this.fontRegistry.get(name)
    }

}