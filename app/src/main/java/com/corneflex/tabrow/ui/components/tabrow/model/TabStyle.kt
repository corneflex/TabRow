package com.corneflex.tabrow.ui.components.tabrow.model

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual styling for each tab: shape, typography, borders, sizing, and spacing.
 *
 * @param horizontalPadding Inner horizontal padding inside each tab.
 * @param verticalPadding Inner vertical padding inside each tab.
 * @param itemSpacing Gap between adjacent tabs.
 * @param edgePadding Leading/trailing inset before the first and after the last tab.
 */
data class TabStyle(
    val shape: Shape = RoundedCornerShape(999.dp),
    val selectedTextStyle: TextStyle? = null,
    val unselectedTextStyle: TextStyle? = null,
    val selectedBorder: BorderStroke? = null,
    val unselectedBorder: BorderStroke? = null,
    val minHeight: Dp = 44.dp,
    val horizontalPadding: Dp = 16.dp,
    val verticalPadding: Dp = 10.dp,
    val itemSpacing: Dp = 8.dp,
    val edgePadding: Dp = 8.dp,
)
