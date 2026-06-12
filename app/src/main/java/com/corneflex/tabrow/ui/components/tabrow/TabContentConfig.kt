package com.corneflex.tabrow.ui.components.tabrow

sealed interface TabContentStyle {
    data object Text : TabContentStyle
    data object Icon : TabContentStyle
    data object Image : TabContentStyle
    data object IconText : TabContentStyle
    data object ImageText : TabContentStyle
}

sealed interface TabContentConfig {
    data object Text : TabContentConfig
    data object Icon : TabContentConfig
    data object Image : TabContentConfig
    data object IconText : TabContentConfig
    data object ImageText : TabContentConfig

    data class Adaptive(
        val unselected: TabContentStyle,
        val selected: TabContentStyle,
    ) : TabContentConfig
}

internal fun TabContentConfig.styleFor(selected: Boolean): TabContentStyle {
    return when (this) {
        TabContentConfig.Text -> TabContentStyle.Text
        TabContentConfig.Icon -> TabContentStyle.Icon
        TabContentConfig.Image -> TabContentStyle.Image
        TabContentConfig.IconText -> TabContentStyle.IconText
        TabContentConfig.ImageText -> TabContentStyle.ImageText
        is TabContentConfig.Adaptive -> if (selected) this.selected else this.unselected
    }
}
