package com.parinherm.form.definitions

enum class SashOrientationDef {
    VERTICAL, HORIZONTAL;

    companion object {
        fun mappedOrientation(orientationDef: SashOrientationDef) =
            when(orientationDef){
                VERTICAL -> "vertical"
                HORIZONTAL -> "horizontal"
            }

        fun unMappedOrientation(raw: String) =
            when(raw){
                "vertical" -> VERTICAL
                "horizontal" -> HORIZONTAL
                else -> VERTICAL
            }
    }
}