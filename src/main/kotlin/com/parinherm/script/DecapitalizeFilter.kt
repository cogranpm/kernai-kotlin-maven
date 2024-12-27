package com.parinherm.script

import com.parinherm.ApplicationData
import io.pebbletemplates.pebble.extension.Filter
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

class DecapitalizeFilter : Filter {
    override fun getArgumentNames(): MutableList<String> {
        return mutableListOf()
    }

    override fun apply(
        input: Any?,
        args: MutableMap<String, Any>?,
        self: PebbleTemplate?,
        context: EvaluationContext?,
        lineNumber: Int
    ): Any {
        if(input == null){
			return ""
		}
		return ApplicationData.decapitalize((input.toString()))
    }
}