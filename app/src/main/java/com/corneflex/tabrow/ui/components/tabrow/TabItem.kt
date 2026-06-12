package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

data class TabItem(
    val text: String? = null,
    val icon: ImageVector? = null,
    val image: Painter? = null,
    val contentDescription: String? = text,
)
