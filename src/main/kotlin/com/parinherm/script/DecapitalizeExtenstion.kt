package com.parinherm.script

import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Filter

class DecapitalizeExtenstion : AbstractExtension() {
    override fun getFilters(): MutableMap<String, Filter> {
        return mutableMapOf("decapitalizeFilter" to DecapitalizeFilter())
    }
}