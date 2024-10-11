package com.parinherm.model

import com.parinherm.form.definitions.ViewDef
import io.pebbletemplates.pebble.template.PebbleTemplate

data class PebbleTemplateExpansion(val viewDef: ViewDef, val template: PebbleTemplate, val fileName: String, val folders: List<String> )
