package com.parinherm.form.definitions

enum class SizeDef {
    LARGE, MEDIUM, SMALL;

    companion object {
        fun mappedSize(size: SizeDef) =
            when (size) {
                SizeDef.LARGE -> "LARGE"
                SizeDef.MEDIUM -> "MEDIUM"
                SizeDef.SMALL -> "SMALL"
            }

        fun unMappedSize(raw: String) =
            when (raw) {
                "LARGE" -> SizeDef.LARGE
                "MEDIUM" -> SizeDef.MEDIUM
                "SMALL" -> SizeDef.SMALL
                else -> SizeDef.LARGE
            }
    }
}