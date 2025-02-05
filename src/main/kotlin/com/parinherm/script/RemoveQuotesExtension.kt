package com.parinherm.script

import io.pebbletemplates.pebble.extension.AbstractExtension
import io.pebbletemplates.pebble.extension.Filter
import com.parinherm.ApplicationData
import io.pebbletemplates.pebble.template.EvaluationContext
import io.pebbletemplates.pebble.template.PebbleTemplate

class RemoveQuotesExtension : AbstractExtension() {
    override fun getFilters(): MutableMap<String, Filter> {
        return mutableMapOf("removeQuotes" to RemoveQuotesFilter())
    }
}

class RemoveQuotesFilter: Filter {
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
		return input.toString().replace("\"", "")
    }
}