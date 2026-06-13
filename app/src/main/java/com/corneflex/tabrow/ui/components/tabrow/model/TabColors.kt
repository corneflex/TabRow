package com.corneflex.tabrow.ui.components.tabrow.model

import androidx.compose.ui.graphics.Color

data class TabColors(
    val selectedContentColor: Color,
    val unselectedContentColor: Color,
    val selectedContainerColor: Color = Color.Transparent,
    val unselectedContainerColor: Color = Color.Transparent,
)
