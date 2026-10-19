package com.material.you.widgets.data.models

enum class AppFontFamily {
    DEFAULT,
    BITCOUNT,
    QUICKSAND,
    BALOO_BHAI;

    fun displayName(): String = when (this) {
        DEFAULT -> "System Default"
        BITCOUNT -> "Bitcount Prop Single"
        QUICKSAND -> "Quicksand"
        BALOO_BHAI -> "Baloo Bhai 2"
    }
}
